package org.cly.service;

//import org.apache.shardingsphere.core.parse.old.parser.context.orderby.OrderItem;
import org.cly.dao.UserMapper;
import org.cly.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public List<String> insertData() {
        System.out.println("---------------------------- Insert Data ----------------------------");
        List<String> result = new ArrayList<>(10);
        for (int i = 1; i <= 10; i++) {
            User notice = new User();
            notice.setType(String.valueOf(i));
            notice.setContent(i+"新增了XXX");
            notice.setNotifier(String.valueOf(i));
            userMapper.insertSelective(notice);
            result.add(notice.getNotifier());
        }
        return result;
    }


    @Override
    public void printData() {
        System.out.println("---------------------------- Print Order Data -----------------------");
        List<User> users = userMapper.selectByPrimaryKey();
        for(User user:users){
            System.out.println(user);
        }
    }
}
