package info.kapable.utils.configurationmanager.reporting.web.rest;

import com.codahale.metrics.annotation.Timed;
import info.kapable.utils.configurationmanager.reporting.domain.Process;

import info.kapable.utils.configurationmanager.reporting.repository.ProcessRepository;
import info.kapable.utils.configurationmanager.reporting.web.rest.util.HeaderUtil;
import info.kapable.utils.configurationmanager.reporting.web.rest.util.PaginationUtil;
import io.swagger.annotations.ApiParam;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Process.
 */
@RestController
@RequestMapping("/api")
public class ProcessResource {

    private final Logger log = LoggerFactory.getLogger(ProcessResource.class);

    private static final String ENTITY_NAME = "process";
        
    private final ProcessRepository processRepository;

    public ProcessResource(ProcessRepository processRepository) {
        this.processRepository = processRepository;
    }

    /**
     * POST  /processes : Create a new process.
     *
     * @param process the process to create
     * @return the ResponseEntity with status 201 (Created) and with body the new process, or with status 400 (Bad Request) if the process has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/processes")
    @Timed
    public ResponseEntity<Process> createProcess(@Valid @RequestBody Process process) throws URISyntaxException {
        log.debug("REST request to save Process : {}", process);
        if (process.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new process cannot already have an ID")).body(null);
        }
        Process result = processRepository.save(process);
        return ResponseEntity.created(new URI("/api/processes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /processes : Updates an existing process.
     *
     * @param process the process to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated process,
     * or with status 400 (Bad Request) if the process is not valid,
     * or with status 500 (Internal Server Error) if the process couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/processes")
    @Timed
    public ResponseEntity<Process> updateProcess(@Valid @RequestBody Process process) throws URISyntaxException {
        log.debug("REST request to update Process : {}", process);
        if (process.getId() == null) {
            return createProcess(process);
        }
        Process result = processRepository.save(process);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, process.getId().toString()))
            .body(result);
    }

    /**
     * GET  /processes : get all the processes.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of processes in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/processes")
    @Timed
    public ResponseEntity<List<Process>> getAllProcesses(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Processes");
        Page<Process> page = processRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/processes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /processes/:id : get the "id" process.
     *
     * @param id the id of the process to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the process, or with status 404 (Not Found)
     */
    @GetMapping("/processes/{id}")
    @Timed
    public ResponseEntity<Process> getProcess(@PathVariable Long id) {
        log.debug("REST request to get Process : {}", id);
        Process process = processRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(process));
    }

    /**
     * DELETE  /processes/:id : delete the "id" process.
     *
     * @param id the id of the process to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/processes/{id}")
    @Timed
    public ResponseEntity<Void> deleteProcess(@PathVariable Long id) {
        log.debug("REST request to delete Process : {}", id);
        processRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

}
