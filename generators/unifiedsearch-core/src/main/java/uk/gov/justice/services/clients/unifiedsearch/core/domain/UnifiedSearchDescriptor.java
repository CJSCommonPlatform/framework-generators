package uk.gov.justice.services.clients.unifiedsearch.core.domain;

import java.util.List;

public class UnifiedSearchDescriptor {

    private final String name;
    private final String specVersion;
    private final String service;
    private final String serviceComponent;
    private final List<Event> events;

    public UnifiedSearchDescriptor(final String name,
                                   final String specVersion,
                                   final String service,
                                   final String serviceComponent,
                                   final List<Event> events) {
        this.name = name;
        this.specVersion = specVersion;
        this.service = service;
        this.serviceComponent = serviceComponent;
        this.events = events;
    }

    public String getSpecVersion() {
        return specVersion;
    }

    public String getService() {
        return service;
    }

    public String getServiceComponent() {
        return serviceComponent;
    }

    public List<Event> getEvents() {
        return events;
    }

    public String getName() {
        return name;
    }

}
