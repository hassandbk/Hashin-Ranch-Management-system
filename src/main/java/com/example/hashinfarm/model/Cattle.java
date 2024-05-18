package com.example.hashinfarm.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.sql.Date;
import java.time.LocalDate;

public class Cattle {
    private int cattleId;
    private String tagId;
    private int herdId;
    private String colorMarkings;
    private String name;
    private String gender;
    private Date dateOfBirth;
    private int age;
    private int weightId;
    private String bcs;
    private int breedId;
    private int sireId;
    private int damId;
    private int damsHerd;
    private int siresHerd;
    private BooleanProperty selected;
    private String breedName;

    private String sireName;

    private String damName;

    private String sireHerdName;

    private String damHerdName;

    private String sireBreedName;

    private String damBreedName;

    public Cattle() {
        // Default constructor
    }

    public Cattle(int cattleId, String tagId, int herdID, String colorMarkings, String name, String gender, LocalDate localDate, int age, int weightId, String bcs, int breedId, String breedName, int sireId, String sireName, int damId, String damName, int damsHerd, int siresHerd, String damHerdName, String sireHerdName, String sireBreedName, String damBreedName) {
        // Default constructor implementation
    }

    public Cattle(int cattleId, String tagId, int herdID, String colorMarkings, String name, String gender,
                  Date dateOfBirth, int age, int weightId, String bcs, int breedId, String breedName,
                  int sireId, String sireName, int damId, String damName, int damsHerd, int siresHerd,
                  String damHerdName, String sireHerdName, String sireBreedName, String damBreedName) {
        this.cattleId = cattleId;
        this.tagId = tagId;
        this.herdId = herdID;
        this.colorMarkings = colorMarkings;
        this.name = name;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.age = age;
        this.weightId = weightId;
        this.bcs = bcs;
        this.breedId = breedId;
        this.breedName = breedName;
        this.sireId = sireId;
        this.sireName = sireName;
        this.damId = damId;
        this.damName = damName;
        this.damsHerd = damsHerd;
        this.siresHerd = siresHerd;
        this.sireHerdName = sireHerdName;
        this.damHerdName = damHerdName;
        this.sireBreedName = sireBreedName;
        this.damBreedName = damBreedName;
        this.selected = new SimpleBooleanProperty(false);
    }

    // Getters and setters for all columns
    public int getCattleId() {
        return cattleId;
    }

    public void setCattleId(int cattleId) {
        this.cattleId = cattleId;
    }


    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }


    public int getHerdId() {
        return herdId;
    }

    public void setHerdId(int herdId) {
        this.herdId = herdId;
    }


    public String getColorMarkings() {
        return colorMarkings;
    }

    public void setColorMarkings(String colorMarkings) {
        this.colorMarkings = colorMarkings;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    public Date getDateOfBirth() {
        return Date.valueOf(dateOfBirth.toLocalDate());
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }


    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }


    public int getWeightId() {
        return weightId;
    }

    public void setWeightId(int weightId) {
        this.weightId = weightId;
    }


    public String getBcs() {
        return bcs;
    }

    public void setBcs(String bcs) {
        this.bcs = bcs;
    }


    public int getBreedId() {
        return breedId;
    }

    public void setBreedId(int breedId) {
        this.breedId = breedId;
    }


    public int getSireId() {
        return sireId;
    }

    public void setSireId(int sireId) {
        this.sireId = sireId;
    }


    public int getDamId() {
        return damId;
    }

    public void setDamId(int damId) {
        this.damId = damId;
    }


    public int getDamsHerd() {
        return damsHerd;
    }

    public void setDamsHerd(int damsHerd) {
        this.damsHerd = damsHerd;
    }


    public int getSiresHerd() {
        return siresHerd;
    }

    public void setSiresHerd(int siresHerd) {
        this.siresHerd = siresHerd;
    }


    public String getBreedName() {
        return breedName;
    }

    public void setBreedName(String breedName) {
        this.breedName = breedName;
    }


    public String getSireName() {
        return sireName;
    }

    public void setSireName(String sireName) {
        this.sireName = sireName;
    }


    public String getDamName() {
        return damName;
    }

    public void setDamName(String damName) {
        this.damName = damName;
    }


    public String getSireHerdName() {
        return sireHerdName;
    }

    public void setSireHerdName(String sireHerdName) {
        this.sireHerdName = sireHerdName;
    }


    public String getDamHerdName() {
        return damHerdName;
    }

    public void setDamHerdName(String damHerdName) {
        this.damHerdName = damHerdName;
    }


    public String getSireBreedName() {
        return sireBreedName;
    }

    public void setSireBreedName(String sireBreedName) {
        this.sireBreedName = sireBreedName;
    }


    public String getDamBreedName() {
        return damBreedName;
    }

    public void setDamBreedName(String damHerdName) {
        this.damBreedName = damBreedName;
    }


    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }
}
