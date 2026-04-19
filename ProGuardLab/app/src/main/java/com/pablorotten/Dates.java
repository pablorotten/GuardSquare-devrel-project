package com.pablorotten;
// "bookingdates":{"checkin":"2026-02-13","checkout":"2026-03-25"}
public class Dates {
  String checkin;
  String checkout;

  @Override
  public String toString() {
    return "checkin=" + checkin + ", checkout=" + checkout;
  }
}