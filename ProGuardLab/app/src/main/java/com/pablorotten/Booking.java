package com.pablorotten;

import com.google.gson.annotations.SerializedName;

public class Booking {
  @SerializedName("firstname")
  String firstname;
  @SerializedName("lastname")
  String lastName;
  @SerializedName("totalprice")
  Integer totalPrice;
  @SerializedName("depositpaid")
  Boolean depositPaid;
  @SerializedName("bookingdates")
  Dates bookingDates;
  @SerializedName("additionalneeds")
  String additionalNeeds;

  @Override
  public String toString() {
    return "First Name: " + firstname + ", Last Name: " + lastName + ", Total Price: " + totalPrice +
        ", Deposit Paid: " + depositPaid + ", Booking Dates: " + bookingDates + ", Additional Needs: " + additionalNeeds;
  }
  }