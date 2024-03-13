package school;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class StudentServiceImp extends UnicastRemoteObject
implements StudentService{
    private List<Student> studentList;
    public StudentServiceImp() throws RemoteException{
        super();
        studentList=new ArrayList<>();
    }
    @Override
    public void addStudent(String name, int age) throws RemoteException {
        studentList.add(new Student(name, age));       
    }
    @Override
    public void removeStudent(String name) throws RemoteException {
        studentList.removeIf(student -> student.getName().equals(name));   
    }
    @Override
    public List<Student> getAllStudents() throws RemoteException {
        return studentList; 
    }
    
}
