package org.micros.organization.services;

import org.micros.organization.events.source.SimpleSourceBean;
import org.micros.organization.model.Organization;
import org.micros.organization.repository.OrganizationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrganizationService {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationService.class);

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private Tracer tracer;

    @Autowired
    SimpleSourceBean simpleSourceBean;


    public Organization getOrg(String orgId) {
        Span newSpan = tracer.createSpan("getOrgDBCall");

        logger.debug("In the organizationService.getOrg() call");
        try {
            return organizationRepository.findById(orgId);
        } finally {
            newSpan.tag("peer.service", "mysql");
            newSpan.logEvent(Span.CLIENT_RECV);
            tracer.close(newSpan);
        }

    }

    public void saveOrg(Organization org) {
        org.setId(UUID.randomUUID().toString());
        organizationRepository.save(org);
        simpleSourceBean.publishOrgChange("SAVE", org.getId());
    }

    public void updateOrg(Organization org) {
        organizationRepository.save(org);
        simpleSourceBean.publishOrgChange("UPDATE", org.getId());
    }

    public void deleteOrg(String orgId) {
        organizationRepository.delete(orgId);
        simpleSourceBean.publishOrgChange("DELETE", orgId);
    }
}
