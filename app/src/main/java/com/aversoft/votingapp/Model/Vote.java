package com.aversoft.votingapp.Model;

import java.io.Serializable;

public class Vote implements Serializable {
    String name, startAt, endAt;
    long date;
    String candidateOne, candidateTwo, candidateThree, candidateFour, candidateFive;

    public Vote() {
    }

    public Vote(String name, String startAt, String endAt, long date, String candidateOne, String candidateTwo) {
        this.name = name;
        this.startAt = startAt;
        this.endAt = endAt;
        this.date = date;
        this.candidateOne = candidateOne;
        this.candidateTwo = candidateTwo;
    }

    public Vote(String name, String startAt, String endAt, long date, String candidateOne, String candidateTwo, String candidateThree) {
        this.name = name;
        this.startAt = startAt;
        this.endAt = endAt;
        this.date = date;
        this.candidateOne = candidateOne;
        this.candidateTwo = candidateTwo;
        this.candidateThree = candidateThree;
    }

    public Vote(String name, String startAt, String endAt, long date, String candidateOne, String candidateTwo, String candidateThree, String candidateFour) {
        this.name = name;
        this.startAt = startAt;
        this.endAt = endAt;
        this.date = date;
        this.candidateOne = candidateOne;
        this.candidateTwo = candidateTwo;
        this.candidateThree = candidateThree;
        this.candidateFour = candidateFour;
    }

    public Vote(String name, String startAt, String endAt, long date, String candidateOne, String candidateTwo, String candidateThree, String candidateFour, String candidateFive) {
        this.name = name;
        this.startAt = startAt;
        this.endAt = endAt;
        this.date = date;
        this.candidateOne = candidateOne;
        this.candidateTwo = candidateTwo;
        this.candidateThree = candidateThree;
        this.candidateFour = candidateFour;
        this.candidateFive = candidateFive;
    }

    public void setCandidateThree(String candidateThree) {
        this.candidateThree = candidateThree;
    }

    public void setCandidateFour(String candidateFour) {
        this.candidateFour = candidateFour;
    }

    public void setCandidateFive(String candidateFive) {
        this.candidateFive = candidateFive;
    }

    public String getName() {
        return name;
    }

    public String getCandidateOne() {
        return candidateOne;
    }

    public String getCandidateTwo() {
        return candidateTwo;
    }

    public String getCandidateThree() {
        return candidateThree;
    }

    public String getCandidateFour() {
        return candidateFour;
    }

    public String getCandidateFive() {
        return candidateFive;
    }

    public long getDate() {
        return date;
    }

    public String getStartAt() {
        return startAt;
    }

    public String getEndAt() {
        return endAt;
    }
}
