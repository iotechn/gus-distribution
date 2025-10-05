package com.dobbinsoft.gus.distribution.utils;

import com.dobbinsoft.gus.distribution.data.dto.session.BoSessionInfoDTO;
import com.dobbinsoft.gus.distribution.data.dto.session.FoSessionInfoDTO;

public class SessionUtils {

    private static final ThreadLocal<BoSessionInfoDTO> BO_USER_INFO_THREAD_LOCAL = new InheritableThreadLocal<>();
    private static final ThreadLocal<FoSessionInfoDTO> FO_USER_INFO_THREAD_LOCAL = new InheritableThreadLocal<>();

    public static void putBoSession(BoSessionInfoDTO infoDTO) {
        BO_USER_INFO_THREAD_LOCAL.set(infoDTO);
    }

    public static BoSessionInfoDTO getBoSession() {
        return BO_USER_INFO_THREAD_LOCAL.get();
    }

    public static void removeBoSession() {
        BO_USER_INFO_THREAD_LOCAL.remove();
    }


    public static void putFoSession(FoSessionInfoDTO infoDTO) {
        FO_USER_INFO_THREAD_LOCAL.set(infoDTO);
    }

    public static FoSessionInfoDTO getFoSession() {
        return FO_USER_INFO_THREAD_LOCAL.get();
    }

    public static void removeFoSession() {
        FO_USER_INFO_THREAD_LOCAL.remove();
    }


}
