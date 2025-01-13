package org.example;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import redis.clients.jedis.JedisPool;
import java.util.List;


@Path("/students")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StudentResource {

    private final StudentDAO studentDAO;
    private final JedisPool jedisPool;

    public StudentResource(StudentDAO studentDAO, JedisPool jedisPool) {
        this.studentDAO = studentDAO;
        this.jedisPool = jedisPool;
    }

    @POST
    public Response createStudent(Student student) {
        studentDAO.createStudent(student);
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    @Path("/{id}")
    public Response getStudent(@PathParam("id") int id) {
        ObjectMapper objectMapper = new ObjectMapper();
        try (var jedis = jedisPool.getResource()) {
            // Check Redis cache
            String cachedStudent = jedis.get("student:" + id);
            if (cachedStudent != null) {
                try {
                    Student cachedStudentObj = objectMapper.readValue(cachedStudent, Student.class);
                    return Response.ok(cachedStudentObj).build(); // Return cached student as an object
                } catch (JsonProcessingException e) {
                    // Handle JSON parsing error for cached student
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity("Error parsing cached student data").build();
                }
            }

            Student student = studentDAO.getStudentById(id);
            if (student != null && student.getId() == id) {
                try {
                    String studentJson = objectMapper.writeValueAsString(student);
                    jedis.setex("student:" + id, 10, studentJson); // Cache for 6 seconds
                    return Response.ok(student).build();
                } catch (JsonProcessingException e) {
                    // Handle JSON conversion error
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity("Error converting student to JSON").build();
                }
            }

            return Response.status(Response.Status.NOT_FOUND).build(); // Student not found
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving student: " + e.getMessage()).build();
        }
    }


    @GET
    public List<Student> getAllStudents() {
        return studentDAO.getAllStudents();
    }

    @PUT
    @Path("/{id}")
    public Response updateStudent(@PathParam("id") int id, Student student) {
        student.setId(id);
        studentDAO.updateStudent(student);
        try (var jedis = jedisPool.getResource()) {
            jedis.del("student:" + id); // Invalidate cache
        }
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteStudent(@PathParam("id") int id) {
        studentDAO.deleteStudent(id);
        try (var jedis = jedisPool.getResource()) {
            jedis.del("student:" + id); // Invalidate cache
        }
        return Response.ok().build();
    }
}

