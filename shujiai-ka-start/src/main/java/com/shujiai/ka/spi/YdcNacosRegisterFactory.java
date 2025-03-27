package com.shujiai.ka.spi;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.registry.Registry;
import org.apache.dubbo.registry.nacos.NacosRegistry;
import org.apache.dubbo.registry.support.AbstractRegistryFactory;

import java.util.Properties;

import static com.alibaba.nacos.api.PropertyKeyConst.*;
import static com.alibaba.nacos.client.naming.utils.UtilAndComs.NACOS_NAMING_LOG_NAME;
import static org.apache.dubbo.common.constants.RemotingConstants.BACKUP_KEY;

/**
 * TODO 业务描述
 *
 * @author xiaokang
 * @date 2024/4/20 16:31
 */
public class YdcNacosRegisterFactory extends AbstractRegistryFactory {

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    protected Registry createRegistry(URL url) {
        return new NacosRegistry(url, buildNamingService(url));
    }


    private NamingService buildNamingService(URL url) {
        Properties nacosProperties = buildNacosProperties(url);
        NamingService namingService;
        try {
            namingService = NacosFactory.createNamingService(nacosProperties);
        } catch (NacosException e) {
            if (logger.isErrorEnabled()) {
                logger.error(e.getErrMsg(), e);
            }
            throw new IllegalStateException(e);
        }
        return namingService;
    }

    private Properties buildNacosProperties(URL url) {
        Properties properties = new Properties();
        setServerAddr(url, properties);
        setProperties(url, properties);

        String username=url.getParameter(USERNAME);
        String password=url.getParameter(PASSWORD);
        if (StringUtils.isNotEmpty(username)) {
            properties.setProperty(USERNAME, username);
        }
        if (StringUtils.isNotEmpty(password)) {
            properties.setProperty(PASSWORD, password);
        }
        return properties;
    }

    private void setServerAddr(URL url, Properties properties) {
        StringBuilder serverAddrBuilder =
                new StringBuilder(url.getHost()) // Host
                        .append(":")
                        .append(url.getPort()); // Port

        // Append backup parameter as other servers
        String backup = url.getParameter(BACKUP_KEY);
        if (backup != null) {
            serverAddrBuilder.append(",").append(backup);
        }

        String serverAddr = serverAddrBuilder.toString();
        properties.put(SERVER_ADDR, serverAddr);
    }

    private void setProperties(URL url, Properties properties) {
        putPropertyIfAbsent(url, properties, NAMESPACE);
        putPropertyIfAbsent(url, properties, NACOS_NAMING_LOG_NAME);
        putPropertyIfAbsent(url, properties, ENDPOINT);
        putPropertyIfAbsent(url, properties, ACCESS_KEY);
        putPropertyIfAbsent(url, properties, SECRET_KEY);
        putPropertyIfAbsent(url, properties, CLUSTER_NAME);
    }

    private void putPropertyIfAbsent(URL url, Properties properties, String propertyName) {
        String propertyValue = url.getParameter(propertyName);
        if (propertyValue != null && propertyValue.trim().length() != 0) {
            properties.setProperty(propertyName, propertyValue);
        }
    }

}
