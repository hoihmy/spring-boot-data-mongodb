package com.bezkoder.spring.data.mongodb.controller;

import com.bezkoder.spring.data.mongodb.model.Tutorial;
import com.bezkoder.spring.data.mongodb.repository.TutorialRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//@CrossOrigin(origins = "http://localhost:8081")
//@CrossOrigin(origins = "*") // Allow all origins
@RestController
@RequestMapping("/api")
public class TutorialController {

    @Autowired
    TutorialRepository tutorialRepository;

    @Autowired
    private MeterRegistry meterRegistry;

    @GetMapping("/tutorials")
    public ResponseEntity<List<Tutorial>> getAllTutorials(@RequestParam(required = false) String title) {
        final long startTime = System.currentTimeMillis();
        try {
            List<Tutorial> tutorials = new ArrayList<Tutorial>();

            if (title == null)
                tutorials.addAll(tutorialRepository.findAll());
            else
                tutorials.addAll(tutorialRepository.findByTitleContaining(title));

            meterRegistry.counter("tutorials.list").increment();

            if (tutorials.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(tutorials, HttpStatus.OK);
        } catch (Exception e) {
            meterRegistry.counter("tutorials.error").increment();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            final long elapsedTime = System.currentTimeMillis() - startTime;
            System.out.println("Elapsed time for fetching all tutorials data: " + elapsedTime);
            meterRegistry.timer("execution.time.tutorials.list")
                    .record(Duration.ofMillis(elapsedTime));
        }
    }

    @GetMapping("/tutorials/{id}")
    public ResponseEntity<Tutorial> getTutorialById(@PathVariable("id") String id) {
        Optional<Tutorial> tutorialData = tutorialRepository.findById(id);

        if (tutorialData.isPresent()) {
            return new ResponseEntity<>(tutorialData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/tutorials")
    public ResponseEntity<Tutorial> createTutorial(@RequestBody Tutorial tutorial) {
        final long startTime = System.currentTimeMillis();
        try {
            Tutorial _tutorial = tutorialRepository.save(new Tutorial(tutorial.getTitle(), tutorial.getDescription(), false));
            meterRegistry.counter("tutorials.create").increment();
            return new ResponseEntity<>(_tutorial, HttpStatus.CREATED);
        } catch (Exception e) {
            meterRegistry.counter("tutorials.error").increment();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            final long elapsedTime = System.currentTimeMillis() - startTime;
            System.out.println("Elapsed time for creating a tutorials data: " + elapsedTime);
            meterRegistry.timer("execution.time.tutorials.create")
                    .record(Duration.ofMillis(elapsedTime));
        }
    }

    @PutMapping("/tutorials/{id}")
    public ResponseEntity<Tutorial> updateTutorial(@PathVariable("id") String id, @RequestBody Tutorial tutorial) {
        Optional<Tutorial> tutorialData = tutorialRepository.findById(id);

        if (tutorialData.isPresent()) {
            Tutorial _tutorial = tutorialData.get();
            _tutorial.setTitle(tutorial.getTitle());
            _tutorial.setDescription(tutorial.getDescription());
            _tutorial.setPublished(tutorial.isPublished());
            return new ResponseEntity<>(tutorialRepository.save(_tutorial), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/tutorials/{id}")
    public ResponseEntity<HttpStatus> deleteTutorial(@PathVariable("id") String id) {
        try {
            tutorialRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/tutorials")
    public ResponseEntity<HttpStatus> deleteAllTutorials() {
        final long startTime = System.currentTimeMillis();
        try {
            tutorialRepository.deleteAll();
            meterRegistry.counter("tutorials.deleteAll").increment();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            meterRegistry.counter("tutorials.error").increment();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            final long elapsedTime = System.currentTimeMillis() - startTime;
            System.out.println("Elapsed time for deleting all tutorials data: " + elapsedTime);
            meterRegistry.timer("execution.time.tutorials.deleteAll")
                    .record(Duration.ofMillis(elapsedTime));
        }
    }

    @GetMapping("/tutorials/published")
    public ResponseEntity<List<Tutorial>> findByPublished() {
        final long startTime = System.currentTimeMillis();
        try {
            List<Tutorial> tutorials = tutorialRepository.findByPublished(true);

            meterRegistry.counter("tutorials.publish").increment();

            if (tutorials.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(tutorials, HttpStatus.OK);
        } catch (Exception e) {
            meterRegistry.counter("tutorials.error").increment();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            final long elapsedTime = System.currentTimeMillis() - startTime;
            System.out.println("Elapsed time for publishing a tutorials data: " + elapsedTime);
            meterRegistry.timer("execution.time.tutorials.deleteAll")
                    .record(Duration.ofMillis(elapsedTime));
        }
    }

}
