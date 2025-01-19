package org.example;

import com.opencsv.CSVWriter;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Path("/export")
@Produces(MediaType.TEXT_PLAIN)
public class ExportResource {

    private final  StudentDAO studentDAO;

    @Inject
    public ExportResource(StudentDAO studentDAO ){
        this.studentDAO=studentDAO;
    }

    @GET
    @Path("/students")
    public Response exportStudentsToCsv() {
        List<Student> students = studentDAO.getAllStudents();
        String csvFilePath = "/Users/trux/Documents/Mayur.csv";

        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath))) {

            String[] header = {"ID", "Name", "Age", "Subject"};
            writer.writeNext(header);

            // Write student data
            for (Student student : students) {
                String[] data = {
                        String.valueOf(student.getId()),
                        student.getName(),
                        String.valueOf(student.getAge()),
                        student.getSubject()
                };
                writer.writeNext(data);
            }

            return Response.ok("CSV file created at: " + csvFilePath).build();

        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error generating CSV file: " + e.getMessage()).build();
        }
    }
}
