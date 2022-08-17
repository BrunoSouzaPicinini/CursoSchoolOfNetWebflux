package br.com.bspicinini.webflux.controller;

import br.com.bspicinini.webflux.entity.Todo;
import br.com.bspicinini.webflux.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.Optional;

@RestController
@RequestMapping("/todos")
public class TodoController {

    private TodoRepository todoRepository;
    private TransactionTemplate transactionTemplate;
    @Qualifier("jdbcScheduler")
    private Scheduler jdbcScheduler;

    public TodoController(TodoRepository todoRepository, TransactionTemplate transactionTemplate, Scheduler jdbcScheduler) {
        this.todoRepository = todoRepository;
        this.transactionTemplate = transactionTemplate;
        this.jdbcScheduler = jdbcScheduler;
    }

    @PostMapping
    public Mono<Todo> save(@RequestBody Todo todo) {
        return Mono.fromCallable(() -> transactionTemplate.execute(action ->
                todoRepository.save(todo)
        ));
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Mono<Todo> findById(@PathVariable("id") Long id) {
        return Mono.justOrEmpty(todoRepository.findById(id));
    }

    @GetMapping
    @ResponseBody
    public Flux<Todo> findAll() {
        return Flux.defer(() -> Flux.fromIterable(todoRepository.findAll())).subscribeOn(jdbcScheduler);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> remove(@PathVariable("id") Long id) {
        return Mono.fromCallable(() -> transactionTemplate.execute(action -> {
            todoRepository.deleteById(id);
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        })).subscribeOn(jdbcScheduler);
    }
}
