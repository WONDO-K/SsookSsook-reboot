package com.stillalive.Ssook_BE.user;

import com.stillalive.Ssook_BE.domain.Child;
import com.stillalive.Ssook_BE.domain.Parent;
import com.stillalive.Ssook_BE.domain.base.User;
import com.stillalive.Ssook_BE.exception.ErrorCode;
import com.stillalive.Ssook_BE.exception.SsookException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user instanceof Parent) {
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_PARENT"));
        } else if (user instanceof Child) {
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_CHILD"));
        }
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getLoginId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // 유저가 부모인지 확인하는 메서드
    public boolean isParent() {
        return user instanceof Parent;
    }

    // 유저가 자식인지 확인하는 메서드
    public boolean isChild() {
        return user instanceof Child;
    }

    // 유저 ID 반환 메서드
    public Integer getChildId() {
        if (user instanceof Child) {
            return ((Child) user).getChildId();
        } else {
            throw new SsookException(ErrorCode.NOT_PARENT_BUT_CHILD);
        }

    }

    // 부모 ID 반환 메서드
    public Integer getParentId() {
        if (user instanceof Parent) {
            return ((Parent) user).getParentId();
        } else {
            throw new SsookException(ErrorCode.NOT_CHILD_BUT_PARENT);
        }
    }

    // 학교 코드 반환
    public Integer getSchoolCode() {
        if (user instanceof Child) {
            return ((Child) user).getSchool().getCode();
        } else {
            throw new SsookException(ErrorCode.NOT_CHILD_BUT_PARENT);
        }
    }

}
