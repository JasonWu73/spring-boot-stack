package net.wuxianjie.web.mybatis;

import java.util.List;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/mybatis")
public class MyBatisController {

    private final MyBatisMapper myBatisMapper;

    public MyBatisController(MyBatisMapper myBatisMapper) {
        this.myBatisMapper = myBatisMapper;
    }

    @GetMapping
    public List<MyBatisData> getAllData() {
        return myBatisMapper.selectAllData();
    }

    @PostMapping
    public MyBatisData addData(@RequestBody @Valid MyBatisData data) {
        myBatisMapper.insertData(data);
        return data;
    }

    @PostMapping("/failed")
    @Transactional(rollbackFor = Exception.class)
    public int addDataWithTransaction(@RequestBody @Valid MyBatisData data) throws Exception {
        myBatisMapper.insertData(data);
        throw new Exception("测试事务回滚");
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllData() {
        myBatisMapper.truncateTable();
        return ResponseEntity.noContent().build();
    }
}
