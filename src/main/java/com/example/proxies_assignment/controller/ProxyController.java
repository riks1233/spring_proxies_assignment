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
@RequestMapping("/api/v1")
public class ProxyController {

	@Autowired
	ProxyRepository proxyRepository;

    @GetMapping("/proxies/all")
    public Response<List<Proxy>> getPaginatedProxies(
        @RequestParam(name = "page") Integer page,
        @RequestParam(name = "per_page") Integer perPage
    ) {
        // TODO: Find a way to do error handling with annotations.
        page -= 1;
        if (page < 0) {
            throw new GenericErrorException("`page` must be greater than or equal to 1.");
        }
        if (perPage < 1) {
            throw new GenericErrorException("`per_page` must be greater than or equal to 1.");
        }
        Pageable pageable = PageRequest.of(page, perPage);
        List<Proxy> proxies = proxyRepository.findAll(pageable).getContent();
        return new SuccessResponse<>(proxies);
    }

    @GetMapping("/proxies/filtered")
    public Response<List<Proxy>> getFilteredProxies(
        @RequestParam(name = "name") String name,
        @RequestParam(name = "type") String type
    ) {
        System.out.println(type.toString());
        List<Proxy> proxies = proxyRepository.findAllFilteredByNameAndTypeLike(name, type);
        return new SuccessResponse<>(proxies);
    }

    @GetMapping("/proxies/{id}")
    public Response<Proxy> getProxy(
        @PathVariable("id") long id
    ) {
        Optional<Proxy> existingProxyOptional = proxyRepository.findById(id);
        if (existingProxyOptional.isPresent()) {
            return new SuccessResponse<Proxy>(existingProxyOptional.get());
        }
        throw new GenericErrorException(String.format("Proxy with id %d does not exist.", id));
    }

    @PostMapping("/proxies")
    public Response<Proxy> createProxy(
        // This validation is problematic, when user sends malformed JSON.
        @Valid @RequestBody Proxy proxy
    ) {
        Proxy savedProxy = proxyRepository.save(proxy);
        return new SuccessResponse<>(savedProxy);
    }

    @PutMapping("/proxies/{id}")
    public Response<Proxy> updateProxy(
        @PathVariable("id") long id,
        // @RequestBody Map<Object, Object> params
        @RequestBody Proxy updatedProxy
    ) {
        Optional<Proxy> existingProxyOptional = proxyRepository.findById(id);
        if (existingProxyOptional.isPresent()) {
            // TODO: Find a way to automate validation with tools.
            Proxy existingProxy = existingProxyOptional.get();
            if (updatedProxy.getName() != null) {
                existingProxy.setName(updatedProxy.getName());
            }
            if (updatedProxy.getType() != null) {
                existingProxy.setType(updatedProxy.getType());
            }
            if (updatedProxy.getHostname() != null) {
                existingProxy.setHostname(updatedProxy.getHostname());
            }
            if (updatedProxy.getPort() != null) {
                existingProxy.setPort(updatedProxy.getPort());
            }
            if (updatedProxy.getUsername() != null) {
                existingProxy.setUsername(updatedProxy.getUsername());
            }
            if (updatedProxy.getPassword() != null) {
                existingProxy.setPasswordWithoutHashing(updatedProxy.getPassword());
            }
            if (updatedProxy.isActive() != null) {
                existingProxy.setActive(updatedProxy.isActive());
            }

            Proxy savedProxy = proxyRepository.save(existingProxy);
            return new SuccessResponse<>(savedProxy);

            // This manual field assignment does not work for enum.

            // params.forEach((k, v) -> {
            //     Field field = ReflectionUtils.findField(Proxy.class, (String) k);
            //     if (field != null) {
            //         field.setAccessible(true);
            //         ReflectionUtils.setField(field, existingProxy, field.getType().cast(v));
            //     }
            // });
            // Set<ConstraintViolation<Proxy>> violations = validator.validate(existingProxy);
            // if (!violations.isEmpty()) {
            //     throw new InvalidPropertyException(violations.iterator().next().getMessage());
            // }

        }
        throw new GenericErrorException(String.format("Proxy with id %d does not exist.", id));
    }

    @DeleteMapping("/proxies/{id}")
    public Response<Proxy> deleteProxy(
        @PathVariable("id") long id
    ) {
        Optional<Proxy> proxy = proxyRepository.findById(id);
        if (proxy.isPresent()) {
            proxyRepository.deleteById(id);
            return new SuccessResponse<>(proxy.get());
        }
        throw new GenericErrorException(String.format("Proxy with id %d does not exist.", id));
    }
}
