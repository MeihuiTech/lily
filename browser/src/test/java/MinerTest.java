import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mei.hui.browser.BrowserApplication;
import com.mei.hui.browser.entity.IdAddresses;
import com.mei.hui.browser.mapper.IdAddressesMapper;
import com.mei.hui.browser.service.BlockService;
import lombok.extern.log4j.Log4j2;
import net.bytebuddy.asm.Advice;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BrowserApplication.class)
@Log4j2
public class MinerTest {

    @Autowired
    private IdAddressesMapper mapper;
    @Autowired
    private BlockService blockService;

    @Test
    public void test(){
        QueryWrapper<IdAddresses> queryWrapper = new QueryWrapper();

        List<IdAddresses> list = mapper.selectList(queryWrapper);
        log.info(JSON.toJSONString(list));
    }

}
