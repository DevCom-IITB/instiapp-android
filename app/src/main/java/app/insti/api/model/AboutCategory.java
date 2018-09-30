package app.insti.api.model;

import java.util.List;

public class AboutCategory {
    private String name;
    private List<AboutIndividual> individuals;

    public AboutCategory(String name, List<AboutIndividual> individuals) {
        this.name = name;
        this.individuals = individuals;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AboutIndividual> getIndividuals() {
        return individuals;
    }

    public void setIndividuals(List<AboutIndividual> individuals) {
        this.individuals = individuals;
    }
}
