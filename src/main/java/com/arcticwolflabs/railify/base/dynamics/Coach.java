package com.arcticwolflabs.railify.base.dynamics;

public class Coach {
    int coach_no;
    String type;
    int no_of_seat;
    String arrangement;

    public Coach(int _coach_no, String _type, int _no_of_seat, String _arrangement) {
        coach_no = _coach_no;
        type = _type;
        no_of_seat = _no_of_seat;
        arrangement = _arrangement;
    }

    public void setCoach_no(int _coach_no) {
        this.coach_no = _coach_no;
    }

    public int getCoach_no() {
        return this.coach_no;
    }

    public void setType(String _type) {
        this.type = _type;
    }

    public String getType() {
        return this.type;
    }

    public void setNo_of_seat(int _no_of_seat) {
        this.no_of_seat = _no_of_seat;
    }

    public int getNo_of_seat() {
        return this.no_of_seat;
    }

    public void setArrangement(String _arrangement) {
        this.arrangement = _arrangement;
    }

    public String getArrangement() {
        return this.arrangement;
    }
}
