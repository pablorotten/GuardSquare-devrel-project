package com.pablorotten;
// {"firstname":"Jim","lastname":"Jones","totalprice":112,"depositpaid":true,"bookingdates":{"checkin":"2026-02-13","checkout":"2026-03-25"}}
public class Booking {
  String firstname;
  String lastname;
  Integer totalprice;
  Boolean depositpaid;
  Dates bookingdates;

  @Override
  public String toString() {
    return "firstname=" + firstname + ", lastname=" + lastname + ", totalprice=" + totalprice +
        ", depositpaid=" + depositpaid + ", bookingdates={" + bookingdates + "}";
  }
}