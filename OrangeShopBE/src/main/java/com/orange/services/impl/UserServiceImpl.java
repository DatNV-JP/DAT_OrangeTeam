package com.orange.services.impl;

import com.orange.exception.GlobalException;
import com.orange.utils.StringUtils;
import com.orange.common.payload.Page;
import com.orange.common.template.EmailTemplate;
import com.orange.domain.dto.UserDTO;
import com.orange.domain.mapper.IUserMapper;
import com.orange.domain.model.Authority;
import com.orange.domain.model.User;
import com.orange.payload.response.UserInfoRespone;
import com.orange.redis.CacheService;
import com.orange.repositories.IAuthorityRepository;
import com.orange.repositories.IUserRepository;
import com.orange.services.IUserService;
import lombok.RequiredArgsConstructor;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements IUserService {

    private final IUserRepository userRepository;
    private final IAuthorityRepository authorityService;
    private final IUserMapper userMapper;
    private final CacheService cacheService;
    private final MaillerServiceImpl maillerService;
    private final PasswordEncoder passwordEncoder;
    @PersistenceUnit
    private EntityManagerFactory emf;

    @Override
    public UserDTO create(UserDTO dto) {
        if (dto.getAvatar().isEmpty()) {dto.setAvatar("defaultavatar.png");}
        User user = userMapper.toEntity(dto);
        //check xem user có id chưa nếu chưa gán bằng người dùng
        if (user.getRoles() == null){
        Set<Authority> authorities = new HashSet<>();
        authorities.add(authorityService.findById(2L).orElse(null));
        user.setRoles(authorities);
        }
        user.setActivate(true);
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        String body = EmailTemplate.generateHtmlEmail("Đăng ký thành công tài khoản Sanvadio",EmailTemplate.formatContentWithNewLines(new String[]{"Chúng tôi xin gửi lời cảm ơn chân thành tới Quý khách vì đã đăng ký tài khoản trên shop quần áo Sanvadio! Chúng tôi rất vui mừng khi có bạn trở thành một phần của cộng đồng mua sắm của chúng tôi", "Sanvadio là một cửa hàng chuyên cung cấp các sản phẩm thời trang chất lượng và đa dạng. Chúng tôi cam kết đem đến cho bạn những trải nghiệm mua sắm tuyệt vời với sản phẩm đa dạng, giá cả phải chăng và dịch vụ chăm sóc khách hàng tốt nhất.Khi đăng nhập vào tài khoản của bạn, bạn có thể tìm thấy các sản phẩm thời trang mới nhất và các chương trình khuyến mãi hấp dẫn đang chờ đón bạn.Hãy bắt đầu khám phá cửa hàng của chúng tôi và tìm cho mình những món đồ yêu thích nhé! Nếu bạn có bất kỳ câu hỏi hoặc thắc mắc nào, đừng ngần ngại liên hệ với chúng tôi qua email hoặc số điện thoại được cung cấp trên trang web","Một lần nữa, chúng tôi xin chân thành cảm ơn Quý khách đã lựa chọn Sanvadio là địa điểm mua sắm của mình. Chúng tôi cam kết sẽ nỗ lực hết sức để mang đến cho Quý khách trải nghiệm mua sắm tuyệt vời và dịch vụ chất lượng cao","<div>Trân trọng,</div> <div>Sanvadio</div>"}));
        UserDTO result = userMapper.toDto(userRepository.save(user));
        maillerService.queue(user.getEmail(),"WellCome to Savandio Shop",body);
        return result;
    }


    @Override
    public UserDTO update(UserDTO dto) {
        User user = userRepository.findByUsername(dto.getUsername()).orElseThrow( () -> GlobalException.throwException("user.error.notFound.message"));
        if (dto.getAvatar() != null) user.setAvatar(dto.getAvatar());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        if (dto.getRoles() != null){
        if (dto.getRoles().get(0).getId() != null) {
            Set<Authority> roles = new HashSet<>();
            for (int i = 0; i < dto.getRoles().size(); i++) {
                roles.add(authorityService.findById(dto.getRoles().get(i).getId()).orElse(null));
            }
            user.setRoles(roles);
        }
        }
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserDTO delete(UserDTO dto) {
        User user = userRepository.findById(dto.getId()).orElse(null);
        user.setActivate(false);
        return userMapper.toDto(userRepository.save(user));
    }


    @Override
    public Page<?> fillAll(Pageable pageable) {
        org.springframework.data.domain.Page<User>  result = userRepository.findAllByUserActive(true,pageable);
        List<UserDTO> dtos = result.getContent()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
        return Page.of(result.getSize(), result.getNumber(), result.getTotalPages(), Math.toIntExact(result.getTotalElements()), dtos);
    }

    @Override
    public UserDTO findById(Long aLong) {
        User user = userRepository.findById(aLong).orElse(null);
        return userMapper.toDto(user);
    }

    @Override
    public UserInfoRespone getUserInfo(String username) {
        User user = findByUsername(username);
        return UserInfoRespone.builder()
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .avatar(user.getAvatar())
                .roles(user.getRoles().stream().map(r -> r.getName().getValue()).collect(Collectors.toSet()))
                .build();
    }

    @Override
    public User findByUsername(String username) {
        return this.userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public User changePass(String userMame, String newPass){
        if(userMame.isEmpty() || newPass.isEmpty()) { throw  GlobalException.throwException("user.change.pass.fail");}
        User user = userRepository.findByUsername(userMame).orElse(null);
        user.setPasswordHash(passwordEncoder.encode(newPass));
        return user;
    }

    @Override
    public void forGotPassWord(String userMame){
        if (userMame.isEmpty()) { throw GlobalException.throwException("user.error.username");}
        User user = userRepository.findByUsername(userMame).orElse(null);
        if (user == null) {
            throw GlobalException.throwException("user.error.notFound.message");
        }
        Random random = new Random();
        int randomNumber = random.nextInt(100000000);
        String value = String.format("%08d", randomNumber);
        cacheService.set(user.getUsername(), value,30000L);
        String body = EmailTemplate.generateHtmlEmail("(Sanvadio) Quên mật khẩu",EmailTemplate.formatContentWithNewLines(new String[]{"Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn. Để hoàn thành quá trình đặt lại mật khẩu, chúng tôi đã tạo một mã OTP (One Time Password - Mật khẩu một lần sử dụng) ngẫu nhiên.","Mã OTP của bạn là: "+value,"Vui lòng nhập mã này trong trang đặt lại mật khẩu của chúng tôi để hoàn thành quá trình đặt lại mật khẩu. Lưu ý rằng mã OTP này chỉ có giá trị trong vòng 5 phút kể từ khi bạn nhận được email này. Nếu bạn không đặt lại mật khẩu, vui lòng bỏ qua email này. Nếu bạn gặp bất kỳ vấn đề hoặc thắc mắc nào, vui lòng liên hệ với chúng tôi qua địa chỉ email của chúng tôi hoặc qua số điện thoại hỗ trợ của chúng tôi.","<div>Trân trọng,</div> <div>Sanvadio</div>"}));
        maillerService.queue(user.getEmail(),"(Sanvadio) Quên mật khẩu",body);
    }

    @Override
    public boolean checkOTP(String userMame, String OTP){
        if (userMame.isEmpty() || OTP.isEmpty()){throw GlobalException.throwException("user.change.pass.fail.otp");}
    String OTP_VALUE_SERVER = (String) cacheService.get(userMame);
    if (OTP.equals(OTP_VALUE_SERVER)){
        return true;
    }
    return false;
    }

    @Override
    public UserDTO disableOrActiveAccount(Long id, Boolean status) {
        User user = userRepository.findById(id).orElse(null);
        user.setActivate(status);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public Page<?> fillAllByStt(Boolean status, Pageable pageable) {
        org.springframework.data.domain.Page<User>  result = userRepository.findAllByUserActive(status,pageable);
        List<UserDTO> dtos = result.getContent()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
        return Page.of(result.getSize(), result.getNumber(), result.getTotalPages(), Math.toIntExact(result.getTotalElements()), dtos);
    }

    @Override
    public List<UserDTO> findByUsernameOrName(String keyWord) {
        List<User> result = userRepository.findAllByUsernamOrName(keyWord);
        return userMapper.toDtoList(result);
    }

    @Override
    public Page<?> search(String keyWord, Pageable pageable) {
        EntityManager em = emf.createEntityManager();

        try {
            SearchSession searchSession = Search.session(em);
            int offset = pageable.getPageNumber() * pageable.getPageSize();
            int limit = pageable.getPageSize();
            SearchResult<User> result = searchSession.search(User.class)
                    .where(f -> f.bool(b -> {
                        b.must(f.match().field("activate").matching(true));
                        if (!StringUtils.isNullOrEmpty(keyWord)) {
                            String wildcard = "*" + keyWord + "*";
                            b.must(f.bool()
                                    .should(f.wildcard().field("username_search").matching(wildcard))
                                    .should(f.wildcard().field("firstName_search").matching(wildcard))
                                    .should(f.wildcard().field("lastName_search").matching(wildcard))
                            );
                        }
                    }))
                    .sort(f -> f.composite(b -> {
                        if (pageable != null && pageable.getSort() != null) {
                            for (Sort.Order order : pageable.getSort()) {
                                if (order.isAscending()) {
                                    b.add(f.field(order.getProperty()).asc());
                                } else {
                                    b.add(f.field(order.getProperty()).desc());
                                }
                            }
                        } else {
                            b.add(f.field("id").desc());
                        }
                    }))
                    .fetch(offset, limit);
            List<User> hits = result.hits();
            Long totalHitCount = result.total().hitCount();
            return com.orange.common.payload.Page.of(
                    pageable.getPageSize(),
                    pageable.getPageNumber(),
                    com.orange.common.payload.Page.calTotalPages(Math.toIntExact(totalHitCount), pageable.getPageSize()),
                    Math.toIntExact(totalHitCount),
                    userMapper.toDtoList(hits));
        } catch (Exception e) {
            e.printStackTrace();
            return com.orange.common.payload.Page.of(0,0,0,0, new ArrayList<>());
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public void indexData() throws InterruptedException {
        EntityManager em = emf.createEntityManager();
        SearchSession searchSession = Search.session(em);
        MassIndexer indexer = searchSession.massIndexer(User.class)
                .threadsToLoadObjects(7);
        indexer.startAndWait();
    }

}
