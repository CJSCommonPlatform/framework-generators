package uk.gov.justice.services.clients.unifiedsearch.generator.domain;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UnifiedSearchDescriptor that = (UnifiedSearchDescriptor) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (specVersion != null ? !specVersion.equals(that.specVersion) : that.specVersion != null)
            return false;
        if (service != null ? !service.equals(that.service) : that.service != null) return false;
        if (serviceComponent != null ? !serviceComponent.equals(that.serviceComponent) : that.serviceComponent != null)
            return false;
        return events != null ? events.equals(that.events) : that.events == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (specVersion != null ? specVersion.hashCode() : 0);
        result = 31 * result + (service != null ? service.hashCode() : 0);
        result = 31 * result + (serviceComponent != null ? serviceComponent.hashCode() : 0);
        result = 31 * result + (events != null ? events.hashCode() : 0);
        return result;
    }
}
