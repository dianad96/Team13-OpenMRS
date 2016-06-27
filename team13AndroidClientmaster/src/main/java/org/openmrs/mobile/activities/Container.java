package org.openmrs.mobile.activities;

import java.util.Date;

/**
 * Created by Diana on 02/02/2016.
 */
public class Container {


    //login in to the bupa server
    public static String url = "http://bupaopenmrs.cloudapp.net/openmrs";

    public static String patient_name = "";
    public static String patient_age = "";
    public static String patient_gender = "";
    public static String patient_birthdate = "";

    public static int total_calories = 0;
    public static String calories_burned = "";
    public static String distance_covered = "";
    public static String total_steps = "";
    public static String floors_climbed = "";
    public static String active_minutes = "";
    public static String heart_rate = "";
    public static String height = "";
    public static String weight = "";
    public static Date latest_message;
    public static boolean is_visible;


    //*For new patient registration*//
    public static String person_identifier = "123";
    public static String person_uuid = "";
    public static String identifier_type = "8d79403a-c2cc-11de-8d13-0010c6dffd0f";
    public static String location_uuid = "";
    public static String patient_uuid = "";
    //*__________________________________*//


    public static String doctor_uuid = "dd73d468-1691-11df-97a5-7038c432aabf";
    public static String user_uuid = "f203f94d-5b02-4798-be06-434c5eb2e26b"; // Raizel
    public static String chevy_user_uuid = "06168cfe-7d77-45b7-b8ba-290201f2ba07";
    public static String chat_uuid="182e8d2d-e710-409c-98e2-f98ec9b7bacb";
    public static String calories_uuid = "919df587-688b-4c20-bad8-9d8ebeaf9cd0";
    public static String steps_uuid = "472224eb-60ac-4865-96fb-4c6848572d0f";
    public static String calories_burned_uuid = "55a95266-318c-4736-be69-39047d5241e7";
    public static String distance_covered_uuid = "59dc8263-d132-408a-8f07-3e6399e8dfa0";
    public static String total_steps_uuid = "472224eb-60ac-4865-96fb-4c6848572d0f";
    public static String floors_climbed_uuid = "b7a30db4-5383-48b5-b28c-dc12e3304f8d";
    public static String active_minutes_uuid = "271e8d30-273f-4e1f-816d-69bcba8742a4";
    public static String heart_rate_uuid = "be4f67b0-1691-11df-97a5-7038c432aabf";
    public static String height_uuid = "18166deb-a22a-43e3-8d79-f311f4c4dddc";
    public static String weight_uuid = "2009fef8-157c-47af-b866-5446d4415d63";
    public static String bmi_uuid = "be4d9ce6-1691-11df-97a5-7038c432aabf";
    public static String URLBase = "http://bupaopenmrs.cloudapp.net/openmrs/ws/rest/v1/";
    public static String username = "admin";
    public static String password = "Admin123";

    public static String breakfast_input = "";
    public static String lunch_input = "";
    public static String diner_input = "";
    public static String meal_choice = "";
    public static int food_calories_breakfast = 0;
    public static int food_calories_lunch = 0;
    public static int food_calories_dinner = 0;
}
