## Spring REST -CRUD Database Real-Time Project
>參考網址：https://ithelp.ithome.com.tw/articles/10196136
>
>### REST是一個設計理念
REST把所有WEB上的東西都以一個資源(Resource)去看待，並且所有資源都會有一個URI(Uniform Resource Identifier),RESTful Web Service,符合REST Web服務允許系統使用統一和預定的無狀態操作(Representational state transfer)集訪問和操作Web資源
>
>對資源的操作會支援HTTP請求方法 (例如GET, POST, PUT, DELETE)
>
>### RESUful傳輸架構風格最重要的架構約束有6個：
>
>* 客戶-伺服器（Client-Server）
>* 無狀態機制（Stateless）
>* 快取（Cache）
>* 統一介面（Uniform Interface）
>* 分層系統（Layered System）
>* 按需代碼（Code-On-Demand，可選）
>
>### RESTful設計風格的優點
>* 利用快取來提升回應速度
>* 伺服器可以處理不同的數據返回請求，提高伺服器的擴充功能性
>* 瀏覽器即可作為用戶端，ex:我不需要寫一個client的軟體來讓client去做連線
>* 相對於其他疊加在HTTP協定之上的機制，REST的軟體相依性更小
>* 不需要額外的資源發現機制
>* 易維護，擴展性好，串接服務容易


**pom.xml相關依賴，主要包含Spring MVC、Jackson、Hibernate、MySQL以及C3PO等依賴資訊**
```
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">

	<modelVersion>4.0.0</modelVersion>
	<groupId>com.luv2code.springdemo</groupId>
	<artifactId>spring-crm-rest</artifactId>
	<version>1.0.0</version>
	<packaging>war</packaging>

	<properties>
		<springframework.version>5.0.6.RELEASE</springframework.version>
		<hibernate.version>5.4.1.Final</hibernate.version>
		<mysql.connector.version>5.1.45</mysql.connector.version>
		<c3po.version>0.9.5.2</c3po.version>

		<maven.compiler.source>1.8</maven.compiler.source>
		<maven.compiler.target>1.8</maven.compiler.target>
	</properties>

	<dependencies>

		<!-- Spring -->
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-webmvc</artifactId>
			<version>${springframework.version}</version>
		</dependency>

		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-tx</artifactId>
			<version>${springframework.version}</version>
		</dependency>
		
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-orm</artifactId>
			<version>${springframework.version}</version>
		</dependency>

		<!-- Add Jackson for JSON converters -->
		<dependency>
			<groupId>com.fasterxml.jackson.core</groupId>
			<artifactId>jackson-databind</artifactId>
			<version>2.9.5</version>
		</dependency>

		<!-- Hibernate -->
		<dependency>
			<groupId>org.hibernate</groupId>
			<artifactId>hibernate-core</artifactId>
			<version>${hibernate.version}</version>
		</dependency>

		<!-- MySQL -->
		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
			<version>${mysql.connector.version}</version>
		</dependency>

		<!-- C3PO -->
		<dependency>
			<groupId>com.mchange</groupId>
			<artifactId>c3p0</artifactId>
			<version>${c3po.version}</version>
		</dependency>

		<!-- Servlet+JSP+JSTL -->
		<dependency>
			<groupId>javax.servlet</groupId>
			<artifactId>javax.servlet-api</artifactId>
			<version>3.1.0</version>
		</dependency>

		<dependency>
			<groupId>javax.servlet.jsp</groupId>
			<artifactId>javax.servlet.jsp-api</artifactId>
			<version>2.3.1</version>
		</dependency>


		<!-- to compensate for java 9 not including jaxb -->
		<dependency>
		    <groupId>javax.xml.bind</groupId>
		    <artifactId>jaxb-api</artifactId>
		    <version>2.3.0</version>
		</dependency>

	</dependencies>

	<build>

		<finalName>spring-crm-rest</finalName>

		<plugins>

			<!-- Builds a Web Application Archive (WAR) file from the project output 
				and its dependencies. -->
			<plugin>
				<!-- Add Maven coordinates (GAV) for: maven-war-plugin -->
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-war-plugin</artifactId>
				<version>3.2.0</version>
			</plugin>

		</plugins>
	</build>
</project>
```

**MySpringMvcDispatcherServletInitializer.java完整程式碼**
>擴展`AbstractAnnotationConfigDispatcherServletInitializer`類別，重寫`getServletConfigClasses`方法，給定我們自定義配置類別以及URL根目錄配置，在應用程式初始化時，動態進行 Servlet的建立、設定與註冊
```
public class MySpringMvcDispatcherServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	protected Class<?>[] getRootConfigClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class[] { DemoAppConfig.class };
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}
}
```

**DemoAppConfig.java完整程式碼**
>標記為配置類別，給定類別掃描包路徑、資料庫相關資料文件路徑，配置DataSource相關屬性，包含JDBC Driver、用戶名稱、密碼、資料庫URL以及連接池相關參數等等。在系統啟動時，Spring會自動為我們配置、創建以及初始化。同時建立SessionFactory(Hibernate工廠)的Bean以及事務管理器自動為我們進行會話啟閉，減少程式碼的撰寫數量
```
@Configuration
@EnableWebMvc
@EnableTransactionManagement
@ComponentScan("com.luv2code.springdemo")
@PropertySource({ "classpath:persistence-mysql.properties" })
public class DemoAppConfig implements WebMvcConfigurer {

	@Autowired
	private Environment env;
	
	private Logger logger = Logger.getLogger(getClass().getName());
	
	// define a bean for ViewResolver

	@Bean
	public DataSource myDataSource() {
		
		// create connection pool
		ComboPooledDataSource myDataSource = new ComboPooledDataSource();

		// set the jdbc driver
		try {
			myDataSource.setDriverClass("com.mysql.jdbc.Driver");		
		}
		catch (PropertyVetoException exc) {
			throw new RuntimeException(exc);
		}
		
		// for sanity's sake, let's log url and user ... just to make sure we are reading the data
		logger.info("jdbc.url=" + env.getProperty("jdbc.url"));
		logger.info("jdbc.user=" + env.getProperty("jdbc.user"));
		
		// set database connection props
		myDataSource.setJdbcUrl(env.getProperty("jdbc.url"));
		myDataSource.setUser(env.getProperty("jdbc.user"));
		myDataSource.setPassword(env.getProperty("jdbc.password"));
		
		// set connection pool props
		myDataSource.setInitialPoolSize(getIntProperty("connection.pool.initialPoolSize"));
		myDataSource.setMinPoolSize(getIntProperty("connection.pool.minPoolSize"));
		myDataSource.setMaxPoolSize(getIntProperty("connection.pool.maxPoolSize"));		
		myDataSource.setMaxIdleTime(getIntProperty("connection.pool.maxIdleTime"));

		return myDataSource;
	}
	
	private Properties getHibernateProperties() {

		// set hibernate properties
		Properties props = new Properties();

		props.setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));
		props.setProperty("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
		
		return props;				
	}

	
	// need a helper method 
	// read environment property and convert to int
	
	private int getIntProperty(String propName) {
		
		String propVal = env.getProperty(propName);
		
		// now convert to int
		int intPropVal = Integer.parseInt(propVal);
		
		return intPropVal;
	}	
	
	@Bean
	public LocalSessionFactoryBean sessionFactory(){
		
		// create session factorys
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		
		// set the properties
		sessionFactory.setDataSource(myDataSource());
		sessionFactory.setPackagesToScan(env.getProperty("hibernate.packagesToScan"));
		sessionFactory.setHibernateProperties(getHibernateProperties());
		
		return sessionFactory;
	}
	
	@Bean
	@Autowired
	public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
		
		// setup transaction manager based on session factory
		HibernateTransactionManager txManager = new HibernateTransactionManager();
		txManager.setSessionFactory(sessionFactory);

		return txManager;
	}	
}
```

**Customer.java完整程式碼**
>創建Customer實體類別項目，透過`@Entity`、`@Table`註解將實體類別中的相關資料與資料庫中的字段相互映射，以及基本的Getter、Setter方法
```
@Entity
@Table(name="customer")
public class Customer {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private int id;
	
	@Column(name="first_name")
	private String firstName;
	
	@Column(name="last_name")
	private String lastName;
	
	@Column(name="email")
	private String email;
	
	public Customer() {
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "Customer [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email + "]";
	}
}
```

**CustomerDAO.java完整程式碼**
>創建DAO接口，在接口中我們有四個方法，包含獲取所有成員資料、獲取特定成員資料、儲存或更新成員以及刪除成員等基本動作
```
public interface CustomerDAO {

	public List<Customer> getCustomers();

	public void saveCustomer(Customer theCustomer);

	public Customer getCustomer(int theId);

	public void deleteCustomer(int theId);
}
```

**CustomerDAOImpl.java完整程式碼**
>創建完接口後必須實現接口方法，透過`@Autowired`注入`SessionFactory`，也就是來自於我們在配置類別中所創建的Bean，並實際覆寫相關方法
```
@Repository
public class CustomerDAOImpl implements CustomerDAO {

	// need to inject the session factory
	@Autowired
	private SessionFactory sessionFactory;
			
	@Override
	public List<Customer> getCustomers() {
		
		// get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
				
		// create a query  ... sort by last name
		Query<Customer> theQuery = 
				currentSession.createQuery("from Customer order by lastName",
											Customer.class);
		
		// execute query and get result list
		List<Customer> customers = theQuery.getResultList();
				
		// return the results		
		return customers;
	}

	@Override
	public void saveCustomer(Customer theCustomer) {

		// get current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// save/upate the customer ... finally LOL
		currentSession.saveOrUpdate(theCustomer);
		
	}

	@Override
	public Customer getCustomer(int theId) {

		// get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// now retrieve/read from database using the primary key
		Customer theCustomer = currentSession.get(Customer.class, theId);
		
		return theCustomer;
	}

	@Override
	public void deleteCustomer(int theId) {

		// get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// delete object with primary key
		Query theQuery = 
				currentSession.createQuery("delete from Customer where id=:customerId");
		theQuery.setParameter("customerId", theId);
		
		theQuery.executeUpdate();		
	}
}
```

**CustomerService.java完整程式碼**
>創建服務層基本接口，此接口中的方法與`CustomerDAO`接口相同，因我們只是委託調用，從Service到CustomerDAO
```
public interface CustomerService {

	public List<Customer> getCustomers();

	public void saveCustomer(Customer theCustomer);

	public Customer getCustomer(int theId);

	public void deleteCustomer(int theId);
}
```

**CustomerServiceImpl.java完整程式碼**
>同樣完成實現方法，透過`@Autowired`注入customerDAO，實際上此處Service的接口方法實現，就只是委託調用customerDAO的實現方法。
```
@Service
public class CustomerServiceImpl implements CustomerService {

	// need to inject customer dao
	@Autowired
	private CustomerDAO customerDAO;
	
	@Override
	@Transactional
	public List<Customer> getCustomers() {
		return customerDAO.getCustomers();
	}

	@Override
	@Transactional
	public void saveCustomer(Customer theCustomer) {

		customerDAO.saveCustomer(theCustomer);
	}

	@Override
	@Transactional
	public Customer getCustomer(int theId) {
		
		return customerDAO.getCustomer(theId);
	}

	@Override
	@Transactional
	public void deleteCustomer(int theId) {
		
		customerDAO.deleteCustomer(theId);
	}
}
```

**CustomerRestController.java完整程式碼**
>配置所有方法映射路徑，並添加錯誤處理機制，透過`CustomerRestExceptionHandler`自定義全域錯誤處理，當請求資訊錯誤則拋出自定義訊息告知使用者
```
@RestController
@RequestMapping("/api")
public class CustomerRestController {
	
	// autowire the CustomerService
	@Autowired
	private CustomerService customerService;
	
	// add mapping for GET /customers
	@GetMapping("/customers")
	public List<Customer> getCustomers() {
		
		return customerService.getCustomers();
	}
	
	// add mapping for GET /customers/{customerId}
	@GetMapping("/customers/{customerId}")
	public Customer getCustomer(@PathVariable int customerId) {
		
		Customer theCustomer = customerService.getCustomer(customerId);
		
		if (theCustomer == null) {
			throw new CustomerNotFoundException("Customer id not found - " + customerId);
		}
		
		return theCustomer;
	}
	
	// add mapping for POST /customers - add new customer
	@PostMapping("/customers")
	public Customer addCustomer(@RequestBody Customer theCustomer) {
		
		// also just in case the pass an id in JSON ... set id to 0
		// this is force a save of new item ... instead of update
		theCustomer.setId(0);
		
		customerService.saveCustomer(theCustomer);
		
		return theCustomer;
	}
	
	// add mapping for PUT /customers - update existing customer
	@PutMapping("/customers")
	public Customer updateCustomer(@RequestBody Customer theCustomer) {
		
		customerService.saveCustomer(theCustomer);
		
		return theCustomer;
	}
	
	// add mapping for DELETE /customers/{customerId} - delete customer
	@DeleteMapping("/customers/{customerId}")
	public String deleteCustomer(@PathVariable int customerId) {
		
		Customer tempCustomer = customerService.getCustomer(customerId);
		
		if (tempCustomer == null) {
			throw new CustomerNotFoundException("Customer id not found - " + customerId);
		}
		
		customerService.deleteCustomer(customerId);
		
		return "Delete customer id - " + customerId;
	}
}
```

![image](https://user-images.githubusercontent.com/101872264/220517414-4e6e5d9f-b03e-4da3-a59d-5414bce92b00.png)


**CustomerErrorResponse.java完整程式碼**
>創建自定義錯誤響應類別，內容包含所需資訊字段`status(錯誤狀態)`、`message(錯誤訊息)`、`timeStamp(時間戳記)`，以及基本Getter與Setter方法

```
public class CustomerErrorResponse {
	
	private int ststus;
	private String message;
	private long timeStamp;
	
	public CustomerErrorResponse() {
		
	}

	public CustomerErrorResponse(int ststus, String message, long timeStamp) {
		this.ststus = ststus;
		this.message = message;
		this.timeStamp = timeStamp;
	}

	public int getStstus() {
		return ststus;
	}

	public void setStstus(int ststus) {
		this.ststus = ststus;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
}
```

**CustomerNotFoundException.java完整程式碼**
>擴展並自定義錯誤類別
```
public class CustomerNotFoundException extends RuntimeException {

	public CustomerNotFoundException() {
	}

	public CustomerNotFoundException(String message) {
		super(message);
	}

	public CustomerNotFoundException(Throwable cause) {
		super(cause);
	}

	public CustomerNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public CustomerNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
```

**CustomerRestExceptionHandler.java完整程式碼**
>創建全域異常處理程序類別，以下包含一般錯誤請求處理以及針對搜索Index超出範圍的錯誤處理，並透過
>
>參考網址：https://juejin.cn/post/6844904168025489421
>
>@ControllerAdvice，是Spring3.2提供的新註解,它是一個Controller增強器,可對controller中被@RequestMapping註解的方法加一些邏輯處理。主要作用有一下三種
>* 通過@ControllerAdvice註解可以將對於控制器的全局異常處理程序放在同一個位置
>* 註解了@ControllerAdvice的類的方法可以使用@ExceptionHandler、@InitBinder、@ModelAttribute註解到方法上
>>* @ExceptionHandler：用於全局處理控制器裡的異常，進行全局異常處理
>>* @InitBinder：用來設置WebDataBinder，用於自動綁定前台請求參數到Model中，全局數據預處理。
>>* @ModelAttribute：本來作用是綁定鍵值對到Model中，此處讓全局的@RequestMapping都能獲得在此處設置的鍵值對，全局數據綁定
>* @ControllerAdvice註解將作用在所有註解了@RequestMapping的控制器的方法上
```
@ControllerAdvice
public class CustomerRestExceptionHandler {

	// Add an exception handler for CustomerNotFoundException
	#創建異常處理程序(針對搜索Index超出範圍)
	@ExceptionHandler
	public ResponseEntity<CustomerErrorResponse> handleException(CustomerNotFoundException exc){
		
		// create CustomerErrorResponse
		CustomerErrorResponse error = new CustomerErrorResponse(
											HttpStatus.NOT_FOUND.value(),
											exc.getMessage(),
											System.currentTimeMillis());
		
		// return ResponseEntity
		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}
	
	// Add another exception handler ... to catch any exception (catch all)
	#創建異常處理程序(對於所有一般錯誤請求處理)
	@ExceptionHandler
	public ResponseEntity<CustomerErrorResponse> handleException(Exception exc){
		
		// create CustomerErrorResponse
		CustomerErrorResponse error = new CustomerErrorResponse(
											HttpStatus.BAD_REQUEST.value(),
											exc.getMessage(),
											System.currentTimeMillis());
		
		// return ResponseEntity
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}
}
```
