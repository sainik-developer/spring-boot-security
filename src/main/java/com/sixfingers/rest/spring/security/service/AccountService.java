package com.sixfingers.rest.spring.security.service;

import com.askfast.commons.agent.intf.AccountServerAgentInterface;
import com.askfast.commons.entity.Account;
import com.askfast.commons.utils.AgentUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Log4j2
@Service
public class AccountService {

    public Optional<Account> getByUsernameAndPassword(final String userName, final String password) {
        final Account account = new Account();
        account.setId("someId");
        account.setUserName("test");
        account.setPassword("test2");
        return Optional.ofNullable(account);
    }

    public Optional<ImmutablePair<String, String>> getByToken(final String refreshToken) {
        return Optional.ofNullable(AgentUtils.getServiceInterfaceByType(AccountServerAgentInterface.class).refreshToken_get_byToken(refreshToken))
                .map(stringObjectMap -> {
                    String accountId = stringObjectMap.get("account").toString();
                    String isEnabled = stringObjectMap.get("enabled").toString();
                    String clientId = stringObjectMap.get("client").toString();
                    if (accountId == null || !Boolean.parseBoolean(isEnabled)) return null;
                    return new ImmutablePair<>(accountId, clientId);
                });
    }
}
