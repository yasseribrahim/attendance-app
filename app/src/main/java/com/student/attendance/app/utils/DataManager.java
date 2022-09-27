package com.student.attendance.app.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.student.attendance.app.R;
import com.student.attendance.app.models.About;
import com.student.attendance.app.models.Grade;
import com.student.attendance.app.models.User;
import com.student.attendance.app.models.UserType;
import com.student.attendance.app.presenters.UsersCallback;
import com.student.attendance.app.presenters.UsersPresenter;

import java.util.ArrayList;
import java.util.List;

public class DataManager {
    public static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static final DatabaseReference NODE_USERS = database.getReference(Constants.NODE_NAME_USERS);

    public static void initUserAdmin() {
        User user = new User();
        user.setId("TSC9qu8h02NVFSenAvo6h0pFjNu1");
        user.setUserType(Constants.USER_TYPE_ADMIN);
        user.setUsername("admin@attendance.com");
        NODE_USERS.child("TSC9qu8h02NVFSenAvo6h0pFjNu1").setValue(user);
    }

    public static void initAbout() {
        About aboutEn = new About();
        About aboutAr = new About();

        aboutEn.setContent("The application displays the university student card electronically and activates the QR code feature, whereby the university can dispense with printed cards and achieve the goals of electronic transformation. And this makes it Easy for extracting the card and not having to print it, as the card is electronic. It is safe to use, as when the plastic card is lost, it can be misused. As for the electronic card, we just change the student’s password.");
        aboutEn.setConditions("The student must have a mobile device and also have the software installed on his device. He must be registered with the university and agree to the terms of use of the application");
        aboutEn.setObjectives("Students and Doctors at the university Lecturers and employees. Retrieval of the students data process becomes easy. The lecturer can manage students in the lecture easily. helping the lecturers take the absence of students more easily. In this way and the process of updating data will be more easily.");
        aboutAr.setContent("يقوم التطبيق بعرض بطاقة الطالب الجامعي إلكترونياً وتفعيل خاصية QR code حيث يمكن للجامعة الاستغناء عن البطاقات المطبوعة وتحقيق أهداف التحول الإلكتروني. وهذا يسهل إخراج البطاقة وعدم الاضطرار إلى طباعتها ، حيث أن البطاقة إلكترونية. إنه آمن للاستخدام ، حيث أنه عند فقدان البطاقة البلاستيكية ، يمكن إساءة استخدامها. بالنسبة للبطاقة الإلكترونية ، نقوم فقط بتغيير كلمة مرور الطالب");
        aboutAr.setConditions("يجب أن يكون لدى الطالب جهاز محمول وأن يكون البرنامج مثبتًا على جهازه أيضًا. يجب أن يكون مسجلاً في الجامعة ويوافق على شروط استخدام التطبيق.");
        aboutAr.setObjectives("الطلاب والأطباء في الجامعة المحاضرون والموظفون. يصبح استرداد عملية بيانات الطالب أمرًا سهلاً. يستطيع المحاضر إدارة الطلاب في المحاضرة بسهولة. مساعدة المحاضرين على استيعاب غياب الطلاب بسهولة أكبر. بهذه الطريقة ستكون عملية تحديث البيانات أكثر سهولة.");

        database.getReference(Constants.NODE_NAME_ABOUT).child("en").setValue(aboutEn);
        database.getReference(Constants.NODE_NAME_ABOUT).child("ar").setValue(aboutAr);
    }

    public static void initUsersLecturers(int counter) {
        UsersPresenter presenter = new UsersPresenter(new UsersCallback() {
            @Override
            public void onFailure(String message, View.OnClickListener listener) {

            }

            @Override
            public void onShowLoading() {

            }

            @Override
            public void onHideLoading() {

            }

            @Override
            public void onGetSignupUserComplete() {
                if (counter < 10) {
                    initUsersLecturers(counter + 1);
                }
            }
        });

        Log.i("DataManager", "Save: " + "student-" + counter + "@card-id.com");
        User user = new User();
        user.setUserType(Constants.USER_TYPE_LECTURER);
        user.setUsername("lecturer-" + counter + "@card-id.com");
        user.setFullName("Agent " + counter);
        user.setAddress("Address " + counter);
        user.setPassword("123456");
        presenter.signup(user);
    }

    public static void initUsersStudents(int grade, int counter) {
        final UsersPresenter presenter = new UsersPresenter(new UsersCallback() {
            @Override
            public void onFailure(String message, View.OnClickListener listener) {

            }

            @Override
            public void onGetUsersComplete(List<User> users) {
                for (User user : users) {
                    user.setFullName(user.getFullName().replace("Agent", "Lecturer"));
                    FirebaseDatabase dp = FirebaseDatabase.getInstance();
                    DatabaseReference node = dp.getReference(Constants.NODE_NAME_USERS);
                    node.child(user.getId()).setValue(user);
                    Log.i("DataManager", "Save: " + user.getUsername());
                }
            }

            @Override
            public void onShowLoading() {

            }

            @Override
            public void onHideLoading() {

            }

            @Override
            public void onGetSignupUserComplete() {
            }
        });

//        Log.i("DataManager", "Save: " + "student-" + counter + "-grade-" + grade + "@card-id.com");
//        User user = new User();
//        user.setUserType(Constants.USER_TYPE_STUDENT);
//        user.setUsername("student-" + counter + "-grade-" + grade + "@card-id.com");
//        user.setFullName("Student " + counter + " Grade " + grade);
//        user.setGradeId(grade);
//        user.setAddress("Address " + counter);
//        user.setPassword("123456");
        presenter.getUsersByType(Constants.USER_TYPE_LECTURER);
    }

    public static List<UserType> getUserTypes(Context context) {
        List<UserType> types = new ArrayList<>();

        for (int i = 1; i <= 4; i++) {
            UserType type = new UserType();
            type.setId(i);
            switch (i) {
                case Constants.USER_TYPE_ADMIN:
                    type.setName(context.getString(R.string.str_user_type_admin));
                    break;
                case Constants.USER_TYPE_STUDENT:
                    type.setName(context.getString(R.string.str_user_type_student));
                    break;
                case Constants.USER_TYPE_LECTURER:
                    type.setName(context.getString(R.string.str_user_type_teacher));
                    break;
                default:
                    type.setName("N/A");
                    break;
            }
            types.add(type);
        }
        return types;
    }

    public static List<Grade> getGrades(Context context) {
        List<Grade> grades = new ArrayList<>();
        String[] gradesNames = context.getResources().getStringArray(R.array.grades);
        for (int i = 1; i <= gradesNames.length; i++) {
            Grade grade = new Grade();
            grade.setId(i);
            grade.setName(gradesNames[i - 1]);
            grades.add(grade);
        }
        return grades;
    }
}
