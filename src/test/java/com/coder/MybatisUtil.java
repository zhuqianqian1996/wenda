package com.coder;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import sun.misc.Resource;

import java.io.InputStream;

/**
 * 为了便于理解mybatis而写的类
 */
public class MybatisUtil {
    private static SqlSessionFactory sqlSessionFactory = null;
    public static SqlSessionFactory getSqlSessionFactory(){
        InputStream inputStream = null;
        if (sqlSessionFactory==null){
            try {
                String resource = "mybatis.xml";
                sqlSessionFactory = new SqlSessionFactoryBuilder().
                        build(Resources.getResourceAsStream(resource));
                return sqlSessionFactory;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return sqlSessionFactory;
    }
}
