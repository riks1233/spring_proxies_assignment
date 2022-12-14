package com.example.proxies_assignment.controller;

import java.util.*;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.proxies_assignment.exception.GenericErrorException;
import com.example.proxies_assignment.http_response.Response;
import com.example.proxies_assignment.http_response.SuccessResponse;
import com.example.proxies_assignment.model.Proxy;
import com.example.proxies_assignment.repository.ProxyRepository;

@RestController
@RequestMapping("/api/v1/proxies")
public class ProxyController {

	@Autowired
	ProxyRepository proxyRepository;

    @GetMapping("")
    public Response<List<Proxy>> getPaginatedProxies(
        @RequestParam(name = "page") Integer page,
        @RequestParam(name = "per_page") Integer perPage
    ) {
        page -= 1;
        if (page < 0) {
            throw new GenericErrorException("`page` must be greater than or equal to 1.");
        }
        if (perPage < 0) {
            throw new GenericErrorException("`per_page` must be greater than or equal to 1.");
        }
        Pageable pageable = PageRequest.of(page, perPage);
        List<Proxy> proxies = proxyRepository.findAll(pageable).getContent();
        return new SuccessResponse<>(proxies);
    }

    @GetMapping("/filtered")
    public Response<List<Proxy>> getFilteredProxies(
        @RequestParam(name = "name") String name,
        @RequestParam(name = "type") String type
    ) {
        List<Proxy> proxies = proxyRepository.findAllFilteredByNameAndTypeLike(name, type);
        return new SuccessResponse<>(proxies);
    }

    @GetMapping("/{id}")
    public Response<Proxy> getProxy(@PathVariable("id") long id) {
        Optional<Proxy> existingProxyOptional = proxyRepository.findById(id);
        if (existingProxyOptional.isPresent()) {
            return new SuccessResponse<Proxy>(existingProxyOptional.get());
        }
        throw new GenericErrorException(proxyDoesNotExistText(id));
    }

    @PostMapping("")
    public Response<Proxy> createProxy(@Valid @RequestBody Proxy proxy) {
        Proxy savedProxy = proxyRepository.save(proxy);
        return new SuccessResponse<>(savedProxy);
    }

    @PutMapping("/{id}")
    public Response<Proxy> updateProxy(
        @PathVariable("id") long id,
        @RequestBody Proxy updatedProxy
    ) {
        Optional<Proxy> existingProxyOptional = proxyRepository.findById(id);
        if (existingProxyOptional.isPresent()) {
            Proxy existingProxy = existingProxyOptional.get();
            existingProxy.updateFieldsFromAnother(updatedProxy);
            Proxy savedProxy = proxyRepository.save(existingProxy);
            return new SuccessResponse<>(savedProxy);
        }
        throw new GenericErrorException(proxyDoesNotExistText(id));
    }

    @DeleteMapping("/{id}")
    public Response<Proxy> deleteProxy(@PathVariable("id") long id) {
        Optional<Proxy> proxy = proxyRepository.findById(id);
        if (proxy.isPresent()) {
            proxyRepository.deleteById(id);
            return new SuccessResponse<>(proxy.get());
        }
        throw new GenericErrorException(proxyDoesNotExistText(id));
    }

    private String proxyDoesNotExistText(long id) {
        return String.format("Proxy with id %d does not exist.", id);
    }
}
