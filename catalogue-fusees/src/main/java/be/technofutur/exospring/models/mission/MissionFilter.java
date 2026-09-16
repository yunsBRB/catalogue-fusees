package be.technofutur.exospring.models.mission;

import be.technofutur.exospring.enums.StatutMission;

public record MissionFilter(String nom, StatutMission statut) {}
