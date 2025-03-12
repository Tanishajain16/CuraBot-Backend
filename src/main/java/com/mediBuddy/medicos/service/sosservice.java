package com.mediBuddy.medicos.service;

import com.mediBuddy.medicos.Exceptions.ResourceNotFoundException;
import com.mediBuddy.medicos.model.SOS;
import com.mediBuddy.medicos.model.User;
import com.mediBuddy.medicos.repositories.sosRepository;
import com.mediBuddy.medicos.repositories.userRepository;
import com.mediBuddy.medicos.utils.AdditionalFeatures;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class sosservice {
    private  final userRepository ur;
    private  final sosRepository sos;
    private final AdditionalFeatures af;


   public String addSOSPerson(String userid,String email){
        Optional<User> ud =   ur.findById(userid);
        if(ud.isEmpty()){
            throw  new ResourceNotFoundException("User Not Found In");
        }
        SOS sd = new SOS();
        sd.setEmail(email);
        sd.setUserId(userid);
        sos.save(sd);
        return  "SOS Created Succccc .";
    }



    public List<SOS> getSos(String userId){
        List<SOS> ans = sos.findByUserId(userId);
        System.out.println("Array Is :-");
        System.out.println(ans);
        return ans;
    }

    public String sossend(String userid){
       List<SOS> all = getSos(userid);
       return af.sendEmail(all,"JECRC University Plot No. IS-2036 to IS-2039 Ramchandrapura Industrial Area Jaipur, Sitapura, Vidhani, Rajasthan 303905");
    }

}
