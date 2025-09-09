package com.example.social_ute.specification;

import com.example.social_ute.entity.User;
import com.example.social_ute.enums.UserStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class UserSpecification {
    public static Specification<User> filterUsers(String q, String role, String status)
    {
        return(root,query,cb) ->{
            List<Predicate> predicates = new ArrayList<>();

            //tim kiem theo ten
            if(q !=null && !q.isEmpty()){
                Predicate fullNameLike = cb.like(cb.lower(root.get("fullName")), "%" + q.toLowerCase() + "%");
                predicates.add(fullNameLike);
            }

            if(role !=null && !role.isEmpty()){
                predicates.add(cb.equal(root.join("role").get("name"), role));
            }

            if (status != null && !status.isEmpty()) {
                predicates.add(cb.equal(root.get("status"), UserStatus.valueOf(status)));
            }


            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
