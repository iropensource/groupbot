package com.iranopensourcecommunity.config;


import com.iranopensourcecommunity.utils.HibernateFacade;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class SharedConfigData {

    private static Map<ServerEnumKey, String> appConfigMap;

    public synchronized static Map<ServerEnumKey, String> getAppConfigs(boolean isDataUpdated) {
        if (appConfigMap == null || isDataUpdated) {
            appConfigMap = new EnumMap<>(ServerEnumKey.class);
            Session session = HibernateFacade.getSessionFactory().getCurrentSession();

            List<ServerConfig> configs = null;
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                NativeQuery<ServerConfig> nativeQuery = session.createNativeQuery("SELECT * FROM application_config", ServerConfig.class);
                configs = nativeQuery.list();
                tx.commit();
            } catch (Exception e) {
                e.printStackTrace();
                if (tx != null)
                    tx.rollback();
            }

            for (ServerConfig ac : configs) {
                appConfigMap.put(ac.getKey(), ac.getValue());
            }
        }

        return appConfigMap;
    }
}
