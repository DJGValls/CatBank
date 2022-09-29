package CatBank.Service;

import CatBank.Repository.StudentCheckingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;


@Transactional
public class StudentCheckingService {

    @Autowired
    StudentCheckingRepository studentCheckingRepository;

}
