package org.example;



import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;


public interface StudentDAO {

    @SqlUpdate("INSERT INTO students (id, name, age, subject) VALUES (:id, :name, :age, :subject)")
    void createStudent(@BindBean Student student);

    @SqlQuery("SELECT * FROM students WHERE id = :id")
    @RegisterBeanMapper(Student.class)
    Student getStudentById(@Bind("id") int id);

    @SqlQuery("SELECT * FROM students")
    @RegisterBeanMapper(Student.class)
    List<Student> getAllStudents();

    @SqlUpdate("UPDATE students SET name = :name, age = :age, subject= :subject WHERE id = :id")
    void updateStudent(@BindBean Student student);

    @SqlUpdate("DELETE FROM students WHERE id = :id")
    void deleteStudent(@Bind("id") int id);
}

