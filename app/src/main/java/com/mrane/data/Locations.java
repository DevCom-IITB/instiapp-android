package com.mrane.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.insti.api.model.Venue;

public class Locations {
    public HashMap<String, Marker> data = new HashMap<String, Marker>();

    public Locations(List<Venue> venueList) {
        // Add locations
        for (Venue venue : venueList) {
            Marker marker;

            // Skip bad locations
            if (venue.getVenuePixelX() == 0 || venue.getVenueGroupId() == null) {
                continue;
            }

            // Set some things up
            if (venue.getVenueParentRelation() == null) {
                venue.setVenueParentRelation("");
            }

            if (venue.getVenueParentRelation() == null || venue.getVenueParentRelation().equals("")) {
                // Add children
                final List<String> children = new ArrayList();
                for (Venue child : venueList) {
                    if (child.getVenueParentId() != null && child.getVenueParentId().equals(venue.getVenueID())) {
                        children.add(child.getVenueName());
                    }
                }
                String[] childArray = new String[children.size()];

                marker = new Building(
                        venue.getVenueName(), venue.getVenueShortName(), venue.getVenuePixelX(), venue.getVenuePixelY(),
                        venue.getVenueGroupId(), children.toArray(childArray), venue.getVenueDescripion());
            } else {
                // Get parent name
                String parentName = "";
                for (Venue parent : venueList) {
                    if (parent.getVenueID().equals(venue.getVenueParentId())) {
                        parentName = parent.getVenueName();
                        break;
                    }
                }

                marker = new Room(venue.getVenueName(), venue.getVenueShortName(), venue.getVenuePixelX(), venue.getVenuePixelY(),
                        venue.getVenueGroupId(), parentName, venue.getVenueParentRelation(), venue.getVenueDescripion());
            }
            data.put(marker.getName(), marker);
        }
    }
}