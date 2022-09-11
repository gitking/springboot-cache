package com.learn.java.cache;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.junit4.SpringRunner;

import com.learn.java.cache.bean.Article;
import com.learn.java.cache.bean.Book;
import com.learn.java.cache.bean.BookEs;
import com.learn.java.cache.bean.Employee;
import com.learn.java.cache.mapper.EmployeeMapper;
import com.learn.java.cache.repository.BookRepository;

import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AppTest {
    @Autowired
    EmployeeMapper employeeMapper;

    @Autowired
    DataSource dataSource;

    @Autowired
    StringRedisTemplate stringRedisTemplate;//操作k-v都是字符串的

    @Autowired
    RedisTemplate redisTemplate;//操作k-v都是对象的

    //@Autowired
    RedisTemplate<Object, Employee> empRedisTemplate;

    @Autowired//自动注入RabbitMQ消息队列
    RabbitTemplate rabbitTemplate;

    /**
     * Redis常见的五大数据类型
     * String(字符串),List(列表),Set(集合),Hash(散列),ZSet(有序集合)
     * stringRedisTemplate.opsForValue();//操作字符串的
     * stringRedisTemplate.opsForList();//操作列表的
     * stringRedisTemplate.opsForSet();//操作集合
     * stringRedisTemplate.opsForHash();//操作Hash的
     * stringRedisTemplate.opsForZSet();//操作有序集合的
     */
    @Test
    public void testRedis() {
        // append 执行redis的append命令
        //stringRedisTemplate.opsForValue().append("testMsg", "hello");
        System.out.println("testMsg的值为:" + stringRedisTemplate.opsForValue().get("testMsg"));
        stringRedisTemplate.opsForList().leftPush("mylist", "1");
        stringRedisTemplate.opsForList().leftPush("mylist", "2");
    }
    @Test
    public void testRedisObj() {
        System.out.println("测试redis保存对象");
        Employee empById = employeeMapper.getEmpById(2);
        //默认将k-v都序列化后保存进redis,使用jdk的序列化机制,所以Employee这个对象必须继承序列化接口
        //redisTemplate.opsForValue().set("emp-01", empById);

        //如果想把对象以json的数据保存进redis里面有俩种方式,
        //1、自己将对象转为json
        //2、修改redistemplate的默认序列化机制
        empRedisTemplate.opsForValue().set("emp-02", empById);
    }

    @Test
    public void dbTest() throws SQLException {
        System.out.println(dataSource.getClass());
        Connection connection = dataSource.getConnection();
        System.out.println(connection);
        connection.close();
    }


    @Test
    public void dataTest() {
        Employee empById = employeeMapper.getEmpById(1);
        System.out.println(empById);
    }


    /*
     * rabbitmq发送单点(direct)一对一消息
     */
    @Test
    public void contextLoads(){
        /*
         * rabbitTemplate.send(exchange, routingKey, message);
         * 第一个参数是指定一个交换器
         * 第二个参数是指定一个路由键key
         * 第三个参数就是消息本身了,可以自定义Message这个类
         */
        //rabbitTemplate.send(exchange, routingKey, message);

        /*
         * 简单常用的是这个方法
         * rabbitTemplate.convertAndSend(exchange, routingKey, object);
         * 第一个参数是指定一个交换器
         * 第二个参数是指定一个路由键key
         * 第三个参数是Object对象,然后convertAndSend会自动把Object对象转化成消息体,自动序列化
         */
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("msg", "这是第一个消息");
        map.put("data", Arrays.asList("helloword",123, true));
        rabbitTemplate.convertAndSend("exchange.direct", "atguigu.news", map);
        Book book = new Book("西游记", "吴承恩");
        rabbitTemplate.convertAndSend("exchange.direct", "atguigu.news", book);
    }

    /*
     * rabbitmq发送单点(fanout)广播消息
     */
    @Test
    public void rabbitMQFanout(){
        /*
         * rabbitTemplate.send(exchange, routingKey, message);
         * 第一个参数是指定一个交换器
         * 第二个参数是指定一个路由键key
         * 第三个参数就是消息本身了,可以自定义Message这个类
         */
        //rabbitTemplate.send(exchange, routingKey, message);

        /*
         * 简单常用的是这个方法
         * rabbitTemplate.convertAndSend(exchange, routingKey, object);
         * 第一个参数是指定一个交换器
         * 第二个参数是指定一个路由键key
         * 第三个参数是Object对象,然后convertAndSend会自动把Object对象转化成消息体,自动序列化
         */
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("msg", "这是第一个广播fanout消息");
        map.put("data", Arrays.asList("helloword",123, true));
        //广播类型的消息不需要指定routingKey路由键,因为广播消息是发给所有队列的
        rabbitTemplate.convertAndSend("exchange.fanout", "", map);
        Book book = new Book("西游记", "吴承恩");
        rabbitTemplate.convertAndSend("exchange.fanout", "", book);
    }

    @Test
    public void receive() {
        //Message message = rabbitTemplate.receive();
        /*
         * 自动转换为对象
         * 从队列atguigu.news中获取消息
         */
        Object o = rabbitTemplate.receiveAndConvert("atguigu.news");
        System.out.println("获取到对象是什么:" + o.getClass());
        System.out.println("获取到的消息是: " + o);
    }

    @Autowired
    AmqpAdmin amqpAdmin;

    /*
     * 使用AmqpAdmin创建交换器
     * AmqpAdmin:创建和删除:Queue队列,Exchange交换器,Binding绑定器
     * AmqpAdmin里面凡是以declare开头的都是创建一些组件,remove和delete删除一些组件
     */
    @Test
    public void createExchange(){
        /*
         * DirectExchange 创建Direct类型的exchange
         * new DirectExchange(String name, boolean durable, boolean autoDelete)
         * DirectExchange第一个参数是exchange的名字,durable是否是持久化的,autoDelete是否自动删除
         */
        amqpAdmin.declareExchange(new DirectExchange("AmqpAdmin.exchange"));
        System.out.println("exchange创建完成");

        //amqpAdmin.declareQueue();这个方法会创建一个队列,队列的名字是随机的
        amqpAdmin.declareQueue(new Queue("AmqpAdmin.queue", true));
        System.out.println("队列创建完成");

        //创建Binding绑定exchange和queue的规则,
        /*
         * new Binding("AmqpAdmin.queue", Binding.DestinationType.QUEUE, "AmqpAdmin.exchange", "amqp.haha", null);
         * 第一个参数是队列的名字,第二个参数是
         * 第三个参数是exchange的名字,第四个参数的名字是路由键
         * 第五个参数是Arguments,这个你可以去页面上看下Exchange绑定的是时候需要输入哪些值
         */
        Binding binding = new Binding("AmqpAdmin.queue", Binding.DestinationType.QUEUE, "AmqpAdmin.exchange", "amqp.haha", null);
        amqpAdmin.declareBinding(binding);
    }


    //JestClient是Elasticsearch的客户端
    //@Autowired
    JestClient jestClinet;

    @Test
    public void testElasticsearch(){
        //1、给Es中索引(保存)一个文档。
        Article article = new Article();
        article.setId(1);
        article.setTitle("文章标题好消息");
        article.setAuthor("文章作者:张三");
        article.setContent("文章内容:helloword	");

        //构建一个Elasticsearch索引
        Index index = new Index.Builder(article).index("atguigu").type("news").build();

        try {
            //执行成功之后在浏览器上直接访问,http://localhost:9200/atguigu/news/1
            jestClinet.execute(index);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test//搜索功能测试
    public void elasticsearchSearch() {
        String json = "{\n"
                + "\"query\":{\n" +
                "	\"match\":{\n" +
                "	\"content\" : \"hello\"\n "+
                "	}\n" +
                "}";
        //构建搜索功能
        Search search = new Search.Builder(json).addIndex("atguigu").addType("news").build();
        try {//执行
            SearchResult searchResult = jestClinet.execute(search);
            //System.out.println("获取命中的记录:" + searchResult.getHits(sourceType));
            System.out.println("获取相关性最高得分:" + searchResult.getMaxScore());
            System.out.println("获取总记录数:" + searchResult.getTotal());
            System.out.println("将结果转换为json字符串:" + searchResult.getJsonString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    BookRepository bookRepository;

    /*
     * 执行成功之后可以在浏览器上直接访问:http://118.24.44.169:9201/atguigu/book/_search
     */
    @Test
    public void testSpringDataElasticsearch(){
        BookEs bookEs = new BookEs();
        bookEs.setId(1);
        bookEs.setBookName("西游记");
        bookEs.setAuthor("吴承恩");;
        bookRepository.index(bookEs);

        for (BookEs book : bookRepository.findByBookNameLike("游")) {
            System.out.println("从Elasticsearch中查到的数据" + book);
        }
    }


    @Autowired
    JavaMailSenderImpl mailSender;

    /*
     * SpringBoot发送邮件代码测试
     */
    @Test
    public void testJavaMail() {
        SimpleMailMessage message = new SimpleMailMessage();
        //邮件设置
        message.setSubject("通知:SpringBoot发送邮件");
        message.setText("SpringBoot设置的邮件内容:今晚7:30开会");
        message.setTo("yale268sh@163.com");//发给谁
        message.setFrom("937243987@qq.com");//谁发的

        mailSender.send(message);
        System.out.println("SpringBoot邮件发送成功");
    }

    //发送带附件的文件
    @Test
    public void testJavaMailMult() throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        //MimeMessageHelper(MimeMessage mimeMessage, boolean multipart)  multipart=true代表要发送带附件的邮件
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        //邮件设置
        helper.setSubject("通知:SpringBoot发送邮件");
        //true代表是否是html样式
        helper.setText("<b style='color:red'>这个文字被加粗了,并且有html的css样式</b>", true);
        helper.setTo("yale268sh@163.com");//发给谁
        helper.setFrom("937243987@qq.com");//谁发的

        //上传文件
        helper.addAttachment("SpringBoot.jpg", new File("C:\\Users\\dell\\Desktop\\微信截图_20201205163354.png"));
        helper.addAttachment("SpringBoot01.jpg", new File("C:\\Users\\dell\\Desktop\\微信截图_20201205163437.png"));

        mailSender.send(mimeMessage);
        System.out.println("SpringBoot邮件发送成功,带附件 的哦");
    }
}
