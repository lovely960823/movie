package nyist.com.project.config.redis;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

/**
 * Redis  key 过期回调
 * @author Administrator
 *
 */
@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

	public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
		super(listenerContainer);
	}
	
	@Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        System.err.println("Redis key:"+expiredKey+"过期回调**********************************************");
    }


}
