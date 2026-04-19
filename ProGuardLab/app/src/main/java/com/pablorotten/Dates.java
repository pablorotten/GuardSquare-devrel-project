package com.pablorotten;
public class Dates {
  String checkin;
  String checkout;

  @Override
  public String toString() {
    return "🛎️ " + checkin + ", ↗️ " + checkout;
  }
}