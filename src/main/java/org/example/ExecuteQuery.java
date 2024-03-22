package org.example;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ExecuteQuery {

    private static final Logger logger = Logger.getLogger(ExecuteQuery.class.getName());


    public HashMap<String, Object> insertCalsdata(String data){



        HashMap<String, Object> resultMap = new HashMap<String, Object>();
        JSONParser parser = new JSONParser();
        JSONObject jsonobj = null;
        Map<String, Object> paramMap=new HashMap<>();
        ArrayList paramList = new ArrayList<>();
        ArrayList insertSFarrayList = new ArrayList<>();//INSERT 문 성공 여부담는 LIST
        Map<String, Object> paramMap_real=new HashMap<>();
        int insertRes=0;
        int insertSuccessResCnt=0;
        int insertFailResCnt=0;

        try {
            jsonobj = (JSONObject) parser.parse(data);
            logger.info("getCalsData__parameters");
            logger.info(String.valueOf(jsonobj));
            paramMap = new org.codehaus.jackson.map.ObjectMapper().readValue(jsonobj.toString(), Map.class);
            logger.info("getCalsData__parameters_codehaus_jacksonmap_result");
            logger.info(paramMap.toString() );
            paramList=(ArrayList)paramMap.get("dataList");
            int listSize=paramList.size();
            logger.info("getCalsData__parametersList_size");
            logger.info(String.valueOf(paramList.size()));
            String insertQueryColumn=HttpServerModule.props.getProperty("insertQueryColumns");
            String[] insertQueryColumnArr=insertQueryColumn.split(",");

            for(int i=0;i<listSize;i++){
                paramMap_real= (HashMap) paramList.get(i);
                StringBuffer insertQuery=new StringBuffer();
                insertQuery.append("INSERT");
                insertQuery.append(" ");
                insertQuery.append("INTO");
                insertQuery.append(" ");
                insertQuery.append(HttpServerModule.props.getProperty("insertQueryTable"));
                insertQuery.append(" ");
                insertQuery.append("(");
                insertQuery.append(insertQueryColumn);
                insertQuery.append(",");
                insertQuery.append(HttpServerModule.props.getProperty("insertQueryColumns_logCol"));//로그컬럽추가
                insertQuery.append(")");
                insertQuery.append(" ");
                insertQuery.append("VALUES");
                insertQuery.append(" ");
                insertQuery.append("(");
                for(int insertQueryColumnIndex=0;insertQueryColumnIndex<insertQueryColumnArr.length;insertQueryColumnIndex++){
                    String paramMap_real_key=insertQueryColumnArr[insertQueryColumnIndex];
                    String insertVal= (String)paramMap_real.get(paramMap_real_key.replaceAll(" ", ""));
                    if(insertVal==null){
                        insertQuery.append(insertVal);
                    }else{
                        String insertValType=insertVal.getClass().getName() ;//변수 타입 체크
                        if(insertValType.contains("String")){//변수 String인경우
                            insertQuery.append("'");//변수가String인경우
                            insertQuery.append(insertVal);
                            insertQuery.append("'");//변수가String인경우
                        }

                    }


                    insertQuery.append(",");
                    if(insertQueryColumnIndex!=insertQueryColumnArr.length-1){

                    }else{//마지막 COLUMN인 경우 INSERT LOG 추가
                        insertQuery.append("to_char(now(), 'yyyy/mm/dd hh24:mi:ss')");
                    }
                }
                insertQuery.append(")");
                logger.info("query:"+insertQuery.toString());


                try {

                    PreparedStatement ps = DbCon.dbconn.prepareStatement(insertQuery.toString());
                    int cnt = ps.executeUpdate();
                    logger.info("쿼리결과:"+cnt);
                    logger.info("insert성공파라미터출력__");
                    logger.info(paramMap_real.toString());
                    insertSFarrayList.add(i,i+"row:성공-->"+paramMap_real.toString());
                    insertSuccessResCnt++;
                } catch (SQLException e) {
                    logger.info("SQLException insert실패파라미터출력__");
                    logger.info(paramMap_real.toString());
                    insertSFarrayList.add(i,i+"row:실패-->"+paramMap_real.toString());
                    insertFailResCnt++;
                }


            }
            if(insertSuccessResCnt == listSize){//전체데이터 인서트 성공시
                //resultMap.put("row",listSize);
                resultMap.put("code","200");//성공
                resultMap.put("msg","전체 데이터 insert 성공");//성공
                resultMap.put("insertDataList",insertSFarrayList);
                resultMap.put("row_sucess_cnt",insertSuccessResCnt);
                resultMap.put("row_fail_cnt",insertFailResCnt);
                resultMap.put("rows_cnt",listSize);
                logger.info("데이터로그");
                logger.info(resultMap.toString());
                return resultMap;
            }else if(insertFailResCnt == listSize){// 모두 실패시
                resultMap.put("code","201");//통신 성공
                resultMap.put("msg"," 데이터 전체 insert 실패");//전체 실패
                resultMap.put("insertDataList",insertSFarrayList);
                resultMap.put("row_sucess_cnt",insertSuccessResCnt);
                resultMap.put("row_fail_cnt",insertFailResCnt);
                resultMap.put("rows_cnt",listSize);
                logger.info("데이터로그");
                logger.info(resultMap.toString());
                return resultMap;

            }else{
                resultMap.put("code","202");//성공
                resultMap.put("msg"," 데이터 일부 insert 실패");//일부 성공
                resultMap.put("insertDataList",insertSFarrayList);
                resultMap.put("row_sucess_cnt",insertSuccessResCnt);
                resultMap.put("row_fail_cnt",insertFailResCnt);
                resultMap.put("rows_cnt",listSize);
                logger.info("데이터로그");
                logger.info(resultMap.toString());
                return resultMap;
            }





        } catch (ParseException e) {
            resultMap.put("code","400");// json 데이터 파싱 처리 오류
            resultMap.put("msg","json 데이터 파싱 처리 오류.");

            e.printStackTrace();
            return resultMap;
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonParseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }






    }












}
