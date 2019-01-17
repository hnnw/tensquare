package com.tensquare.user.controller;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.netflix.discovery.converters.Auto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import com.tensquare.user.pojo.User;
import com.tensquare.user.service.UserService;

import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import util.JwtUtil;

/**
 * 控制器层
 * @author Administrator
 *
 */
@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private StringRedisTemplate redisTemplate;
	@Autowired
	private JwtUtil jwtUtil;
	
	
	/**
	 * 查询全部数据
	 * @return
	 */
	@RequestMapping(method= RequestMethod.GET)
	public Result findAll(){
		return new Result(true,StatusCode.OK,"查询成功",userService.findAll());
	}
	
	/**
	 * 根据ID查询
	 * @param id ID
	 * @return
	 */
	@RequestMapping(value="/{id}",method= RequestMethod.GET)
	public Result findById(@PathVariable String id){
		return new Result(true,StatusCode.OK,"查询成功",userService.findById(id));
	}


	/**
	 * 分页+多条件查询
	 * @param searchMap 查询条件封装
	 * @param page 页码
	 * @param size 页大小
	 * @return 分页结果
	 */
	@RequestMapping(value="/search/{page}/{size}",method=RequestMethod.POST)
	public Result findSearch(@RequestBody Map searchMap , @PathVariable int page, @PathVariable int size){
		Page<User> pageList = userService.findSearch(searchMap, page, size);
		return  new Result(true,StatusCode.OK,"查询成功",  new PageResult<User>(pageList.getTotalElements(), pageList.getContent()) );
	}

	/**
     * 根据条件查询
     * @param searchMap
     * @return
     */
    @RequestMapping(value="/search",method = RequestMethod.POST)
    public Result findSearch( @RequestBody Map searchMap){
        return new Result(true,StatusCode.OK,"查询成功",userService.findSearch(searchMap));
    }
	
	/**
	 * 增加
	 * @param user
	 */
	@RequestMapping(method=RequestMethod.POST)
	public Result add(@RequestBody User user  ){
		userService.add(user);
		return new Result(true,StatusCode.OK,"增加成功");
	}
	
	/**
	 * 修改
	 * @param user
	 */
	@RequestMapping(value="/{id}",method= RequestMethod.PUT)
	public Result update(@RequestBody User user, @PathVariable String id ){
		user.setId(id);
		userService.update(user);		
		return new Result(true,StatusCode.OK,"修改成功");
	}
	
	/**
	 * 删除
	 * @param id
	 */
	@RequestMapping(value="/{id}",method= RequestMethod.DELETE)
	public Result delete(@PathVariable String id ){
		userService.deleteById(id);
		return new Result(true,StatusCode.OK,"删除成功");
	}

	@PostMapping("/sendsms/{mobile}")
	public Result sendSms(@PathVariable("mobile") String phone){
		String key="Sms"+phone;
		String s = redisTemplate.opsForValue().get(key);
		if (!StringUtils.isBlank(s)){
			return new Result(false,StatusCode.ERROR,"请不要重复申请验证码");
		}
		userService.sendSms(phone,key);
		return new Result(true,StatusCode.OK,"发送成功");
	}

	/**
	 * 注册
	 * @param code
	 * @param user
	 * @return
	 */
	@PostMapping("/register/{code}")
	public Result register(@PathVariable("code") String code,@RequestBody User user){
		String key="Sms"+user.getMobile();
		String s = redisTemplate.opsForValue().get(key);
		if (StringUtils.isBlank(s)){
			return new Result(false,StatusCode.ERROR,"请申请验证码");
		}
		if (!code.equals(s)){
			return new Result(false,StatusCode.ERROR,"验证码错误");
		}
		userService.add(user);
		return new Result(true,StatusCode.OK,"注册成功");
	}

	@PostMapping("login")
	public Result login(@RequestBody User admin){
		User admin1=userService.login(admin);
		if (admin1==null){
			return new Result(false,StatusCode.ERROR,"登陆失败");
		}
		String jwt = jwtUtil.createJWT(admin1.getId(),admin1.getNickname(), "user");
		Map<String,String> map = new HashMap<>();
		map.put("rolses","user");
		map.put("token",jwt);
		return new Result(true,StatusCode.OK,"登陆成功",map);
	}

	/**
	 * 增加粉丝数
	 * @param userid
	 * @param x
	 */
	@PostMapping("/incfans/{userid}/{x}")
	public void incFanscount(@PathVariable String userid,@PathVariable int x){
		userService.incFanscount(userid,x);
	}

	/**
	 * 增加关注数
	 * @param userid
	 * @param x
	 */
	@PostMapping("/incfollow/{userid}/{x}")
	public void incFollowcount(@PathVariable String userid,@PathVariable int x){
		userService.incFollowcount(userid,x);
	}
	
}
