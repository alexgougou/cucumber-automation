package Core.Utils;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class GenerateUserInfo
{
    private static final String[] AREA_CODES = {
            "110000", "120000", "130000", "140000", "150000", "210000", "220000", "230000", "310000", "320000",
            "330000", "340000", "350000", "360000", "370000", "410000", "420000", "430000", "440000", "450000",
            "460000", "500000", "510000", "520000", "530000", "540000", "610000", "620000", "630000", "640000"
            // 省略了部分地区码以示例
    };
    private static final int[] WEIGHTS = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2}; // 加权因子
    private static final char[] CHECK_CODES = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'}; // 校验码映射表

    private static final String firstNames = "赵钱孙李周吴郑王冯陈褚卫蒋沈韩杨朱秦尤许何吕施张孔曹严华金魏陶姜戚谢邹喻柏水窦章云苏潘葛奚范彭郎鲁韦昌马苗凤花方俞任袁柳酆鲍史唐费廉岑薛雷贺倪汤滕殷罗毕郝邬安常乐于时傅皮卞齐康伍余元卜顾孟平黄和穆萧尹姚邵湛汪祁毛禹狄米贝明臧计伏成戴谈宋茅庞熊纪舒屈项祝董梁杜阮蓝闵席季";
    private static final String girl = "秀娟英华慧巧美娜静淑惠珠翠雅芝玉萍红娥玲芬芳燕彩春菊兰凤洁梅琳素云莲真环雪荣爱妹霞香月莺媛艳瑞凡佳嘉琼勤珍贞莉桂娣叶璧璐娅琦晶妍茜秋珊莎锦黛青倩婷姣婉娴瑾颖露瑶怡婵雁蓓纨仪荷丹蓉眉君琴蕊薇菁梦岚苑婕馨瑗琰韵融园艺咏卿聪澜纯毓悦昭冰爽琬茗羽希宁欣飘育滢馥筠柔竹霭凝晓欢霄枫芸菲寒伊亚宜可姬舒影荔枝思丽";
    private static final String boy = "伟刚勇毅俊峰强军平保东文辉力明永健世广志义兴良海山仁波宁贵福生龙元全国胜学祥才发武新利清飞彬富顺信子杰涛昌成康星光天达安岩中茂进林有坚和彪博诚先敬震振壮会思群豪心邦承乐绍功松善厚庆磊民友裕河哲江超浩亮政谦亨奇固之轮翰朗伯宏言若鸣朋斌梁栋维启克伦翔旭鹏泽晨辰士以建家致树炎德行时泰盛雄琛钧冠策腾楠榕风航弘";


    /**
     * 生成指定范围的随机整数
     *
     * @param min 最小值
     * @param max 最大值
     */
    public static int genInteger(Integer min, Integer max)
    {
        return (int) (Math.random() * (max - min + 1) + min); // ctrl+1
    }


    /**
     * 随机生成一个常见的汉字字符
     *
     */
    private static char genRandomChineseChar()
    {
        String str = "";
        Random random = new Random();

        int hightPos = (176 + Math.abs(random.nextInt(39)));
        int lowPos = (161 + Math.abs(random.nextInt(93)));

        byte[] b = new byte[2];
        b[0] = (Integer.valueOf(hightPos)).byteValue();
        b[1] = (Integer.valueOf(lowPos)).byteValue();

        try
        {
            str = new String(b, "GBK");
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            System.out.println("错误");
        }
        return str.charAt(0);
    }

    /**
     * 随机字符串：1纯数字，2纯小写，3纯大写，4大小写，5数字字母混合，6汉字字符串
     *
     * @param type 类型
     * @param len  长度
     */
    public static String genString(Integer type, Integer len)
    {
        StringBuilder res = new StringBuilder();
        switch (type)
        {
            case 1:
                for (int i = 0; i < len; i++)
                {
                    res.append(genInteger(0, 9));
                }
                break;
            case 2:
                for (int i = 0; i < len; i++)
                {
                    int min = 'a';
                    int max = 'z';
                    int tmp = genInteger(min, max);
                    res.append((char) tmp);
                }
                break;
            case 3:
                for (int i = 0; i < len; i++)
                {
                    int min = 'A';
                    int max = 'Z';
                    int tmp = genInteger(min, max);
                    res.append((char) tmp);
                }
                break;
            case 4:
                for (int i = 0; i < len; i++)
                {
                    int min = 'A';
                    int max = 'z';
                    int tmp = genInteger(min, max);
                    if (tmp >= 91 && tmp <= 96)
                    {
                        i--;
                        continue;
                    }
                    res.append((char) tmp);
                }
                break;
            case 5:
                for (int i = 0; i < len; i++)
                {
                    int min = '0';
                    int max = 'z';
                    int tmp = genInteger(min, max);
                    if ((tmp >= 91 && tmp <= 96) || (tmp >= 58 && tmp <= 64))
                    {
                        i--;
                        continue;
                    }
                    res.append((char) tmp);
                }
                break;
            case 6:
                for (int i = 0; i < len; i++)
                {
                    res.append(genRandomChineseChar());
                }
                break;

            default:
                throw new RuntimeException("类型不正确");
        }
        return res.toString();
    }

    /**
     * 生成长度随机的邮箱
     *
     */
    public static String genEmail()
    {
        String[] emails = {"126", "163", "qq", "gmail", "hotmail", "outlook"};
        String[] suffix = {"com", "cn", "net", "org"};
        String tmp1 = genString(5, genInteger(6, 18));
        String tmp2 = emails[genInteger(0, emails.length - 1)];
        String tmp3 = suffix[genInteger(0, suffix.length - 1)];
        return tmp1 + "@" + tmp2 + "." + tmp3;
    }

    /**
     * 随机生成电话号码
     *
     */
    public static String genPhoneNum()
    {
        String[] prefix = {"132", "133", "134", "135", "136", "138", "139", "152", "154", "155", "177", "188"};
        int index = genInteger(0, prefix.length - 1);
        return prefix[index] + genString(1, 8);
    }

    /***
     * 生成性别
     */
    public static String genSex()
    {
        int randomNum = new Random().nextInt(2) + 1;
        return randomNum == 1 ? "男" : "女";
    }


    /**
     * 根据性别生成中文名字
     *
     */
    public static String genChineseName(String sex)
    {
        Random random = new Random();
        int index = random.nextInt(firstNames.length());
        String name = "" + firstNames.charAt(index); // 获得一个随机的姓氏

        if (sex.equals("女"))
        {
            int j = random.nextInt(girl.length() - 2);
            if (j % 2 == 0)
            {
                name = name + girl.substring(j, j + 2);
            }
            else
            {
                name = name + girl.charAt(j);
            }
        }
        else
        {
            int j = random.nextInt(boy.length() - 2);
            if (j % 2 == 0)
            {
                name = name + boy.substring(j, j + 2);
            }
            else
            {
                name = name + boy.charAt(j);
            }

        }
        return name;
    }

    /***
     * 根据年龄范围和性别生成身份证号码
     * @param minAge 最小年龄
     * @param maxAge  最大年龄
     * @param sex 性别
     */
    public static String generateIDCard(int minAge, int maxAge, String sex)
    {
        int randomAge = new Random().nextInt((maxAge - minAge) + 1) + minAge;
        LocalDate birthDate = generateRandomBirthDate(randomAge);
        String birthDateStr = birthDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String sequenceCode = generateSequence(sex);
        int randomAreaCodeIndex = new Random().nextInt(AREA_CODES.length);
        String randomAreaCode = AREA_CODES[randomAreaCodeIndex];
        String basePart = randomAreaCode + birthDateStr + sequenceCode; // 前17位数字部分
        char checkCode = calculateCheckCode(basePart); // 校验码
        return basePart + checkCode; // 完整的身份证号
    }

    /***
     * 根据年龄生成生日日期
     * @param age 年龄
     */
    private static LocalDate generateRandomBirthDate(int age)
    {
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        int year = currentDate.getYear() - age;
        Random random = new Random();
        // 在选定的年份内随机选择月份和日期（这里简化为随机月份和日期，实际应用中可以根据需要调整）
        int randomMonth = random.nextInt(12) + 1; // 1到12月
        int randomDay = random.nextInt(30) + 1; // 这里简化为每个月最多30天，实际应用中应考虑每个月的天数不同
        LocalDate randomBirthDate = LocalDate.of(year, randomMonth, randomDay);

        // 确保生成的日期是有效的（例如，不是2月30日）
        if (randomBirthDate.getMonthValue() == 2 && randomDay == 29)
        { // 如果2月29日，调整为2月28日或其他有效日期
            randomDay = 28; // 或者根据需要调整为其他有效日期，例如使用isLeapYear()判断是否为闰年等更复杂的情况处理。
        }
        randomBirthDate = LocalDate.of(year, randomMonth, randomDay);

        return randomBirthDate;

    }

    // 生成顺序码和性别码
    public static String generateSequence(String gender) {
        Random random = new Random();
        // 生成前两位随机数
        int first = random.nextInt(10);
        int second = random.nextInt(10);

        // 生成性别码
        int genderCode = random.nextInt(5) * 2; // 生成偶数
        if ("男".equals(gender)) {
            genderCode += 1; // 转换为奇数
        }

        return String.format("%d%d%d", first, second, genderCode);
    }


    /***
     * 计算身份证的校验值
     */
    private static char calculateCheckCode(String basePart)
    {
        int sum = 0;
        for (int i = 0; i < 17; i++)
        {
            sum += (basePart.charAt(i) - '0') * WEIGHTS[i]; // 加权求和
        }
        int remainder = sum % 11; // 取模得到余数
        return CHECK_CODES[remainder]; // 根据余数查找校验码
    }

}





