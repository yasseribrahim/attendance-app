package com.student.attendance.app.utils;

import android.content.Context;

import com.student.attendance.app.R;

public class UIHelper {
    public static String parseGender(Context context, int id) {
        switch (id) {
            case Constants.GENDER_TYPE_FEMALE:
                return context.getString(R.string.str_gender_type_female);
            default:
                return context.getString(R.string.str_gender_type_male);
        }
    }

    public static String parseUserType(Context context, int id) {
        switch (id) {
            case Constants.USER_TYPE_ADMIN:
                return context.getString(R.string.str_user_type_admin);
            case Constants.USER_TYPE_STUDENT:
                return context.getString(R.string.str_user_type_student);
            case Constants.USER_TYPE_LECTURER:
                return context.getString(R.string.str_user_type_teacher);
            default:
                return "N/A";
        }
    }

    public static String parseGrade(Context context, int id) {
        String[] grades = context.getResources().getStringArray(R.array.grades);
        switch (id) {
            case Constants.GRADE_1:
                return grades[0];
            case Constants.GRADE_2:
                return grades[1];
            case Constants.GRADE_3:
                return grades[2];
            case Constants.GRADE_4:
                return grades[3];
            default:
                return "N/A";
        }
    }

    public static String parseSalaryUnitType(Context context, int id) {
        switch (id) {
            case Constants.SALARY_UNIT_TYPE_HOUR:
                return context.getString(R.string.str_salary_type_hour);
            case Constants.SALARY_UNIT_TYPE_DAY:
                return context.getString(R.string.str_salary_type_day);
            default:
                return context.getString(R.string.str_salary_type_month);
        }
    }
}
