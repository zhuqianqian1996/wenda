package com.nowcoder.prepare;


import org.springframework.stereotype.Service;

@Service
public class WendaService {
    public String getMessage(int useriId){
        return "hello message:"+String.valueOf(useriId);
    }
}
