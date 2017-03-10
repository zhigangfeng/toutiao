package com.newcoder;

import com.newcoder.dao.LoginTicketDAO;
import com.newcoder.model.LoginTicket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)
@WebAppConfiguration
public class ToutiaoApplicationTests {

	@Autowired
	LoginTicketDAO dao;
	@Test
	public void contextLoads() {
		LoginTicket ticket = dao.selectByTicket("aab4c92c8825440abdc3f807f1ca05b3");
		System.out.println(ticket.getId());
		System.out.println(ticket.getUserId());
		//正确放回结果应该是  78 跟67  这个明白吧明白   这个数据不对了？  方法写错了嗯，要测试那个方法啊，我来测试一下selectByTicket 这个方法写错了估计是属性没对上
	}

}
