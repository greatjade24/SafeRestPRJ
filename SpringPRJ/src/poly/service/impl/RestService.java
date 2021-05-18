package poly.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import poly.dto.PagingDTO;
import poly.dto.RestDTO;
import poly.dto.SelfCheckDTO;
import poly.persistance.mapper.IRestMapper;
import poly.persistance.mapper.ISelfCheckMapper;
import poly.service.IRestService;
import poly.util.CmmUtil;
import test.Test;

@Configuration
@EnableScheduling
@Service("RestService")
public class RestService implements IRestService{
	
	@Resource(name="RestMapper")
	private IRestMapper restMapper;
	
	@Resource(name="SelfCheckMapper")
	private ISelfCheckMapper selfCheckMapper;
	
	private Logger log = Logger.getLogger(this.getClass());
	
	@Override
	@Scheduled(cron="0 0 5 ? * MON")  
	public void InsertRestInfo() throws Exception{
		
		RestDTO pDTO = new RestDTO();
		RestDTO rDTO = new RestDTO();
		
		int page = 1; 
	      try{
	    	  
	         while(true){
	            String url = "https://openapi.gg.go.kr/SafetyRestrntInfo?KEY=12200392d4a94e059fec5a6fa315518c&pIndex="+page+"&pSize=1000";
	            
	            DocumentBuilderFactory dbFactoty = DocumentBuilderFactory.newInstance();
	            DocumentBuilder dBuilder = dbFactoty.newDocumentBuilder();
	            Document doc = dBuilder.parse(url);
	            
	            // root tag 
	            doc.getDocumentElement().normalize();
	            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
	            
	            NodeList nList = doc.getElementsByTagName("row");
	            
	            System.out.println("�뙆�떛�븷 由ъ뒪�듃 �닔 : "+ nList.getLength());
	            
	            for(int temp = 0; temp < nList.getLength(); temp++){
	               Node nNode = nList.item(temp);
	               if(nNode.getNodeType() == Node.ELEMENT_NODE){
	                  
	                  Element eElement = (Element) nNode;
	                  System.out.println("######################");
	                  System.out.println("�떇�떦踰덊샇  : " + Test.getTagValue("SAFETY_RESTRNT_NO", eElement));
	                  System.out.println("�긽�샇紐�  : " + Test.getTagValue("BIZPLC_NM", eElement));
	                  System.out.println("援ъ＜�냼  : " + Test.getTagValue("REFINE_LOTNO_ADDR", eElement));
	                  
	                  pDTO.setBizplc_nm(CmmUtil.nvl(Test.getTagValue("BIZPLC_NM", eElement))); //�긽�샇紐�
	                  pDTO.setRefine_lotno_addr(CmmUtil.nvl(Test.getTagValue("REFINE_LOTNO_ADDR", eElement))); //援ъ＜�냼
	                  pDTO.setSafety_restrnt_no(CmmUtil.nvl(Test.getTagValue("SAFETY_RESTRNT_NO", eElement)));//�떇�떦踰덊샇             
	          		  pDTO.setRefine_roadnm_addr(CmmUtil.nvl(Test.getTagValue("REFINE_ROADNM_ADDR", eElement))); //�룄濡쒕챸二쇱냼
	          		  pDTO.setDetail_addr(CmmUtil.nvl(Test.getTagValue("DETAIL_ADDR", eElement)));
	          		  pDTO.setSigngu_nm(CmmUtil.nvl(Test.getTagValue("SIGNGU_NM", eElement)));
	          		  pDTO.setSido_nm(CmmUtil.nvl(Test.getTagValue("SIDO_NM", eElement)));
	          		  pDTO.setIndutype_nm(CmmUtil.nvl(Test.getTagValue("INDUTYPE_NM", eElement)));
	          		  pDTO.setIndutype_detail_nm(CmmUtil.nvl(Test.getTagValue("INDUTYPE_DETAIL_NM", eElement)));//�뾽醫낆긽�꽭紐�
	          		  pDTO.setTelno(CmmUtil.nvl(Test.getTagValue("TELNO", eElement)));//�쟾�솕踰덊샇
	          		  
	          		  rDTO = restMapper.getRestExists(pDTO);
	          		  
	          		  if(rDTO == null) {
	          			  rDTO = new RestDTO();
	          		  }
	          		  
	          		  if(CmmUtil.nvl(rDTO.getExists_yn()).equals("Y")) {
	          			  continue;
	          		  } else {
	          			  restMapper.InsertRestInfo(pDTO);
	          		  }
	          		  
	               }   // for end
	            }   // if end
	           	         	     
	            page += 1;
	            System.out.println("page number : " + page);
	            if(page > 12){ 
	               break;
	            }
	         }   // while end
	         
	         pDTO = null;
	         rDTO = null;
	         
	      } catch (Exception e){   
	         e.printStackTrace();
	      }   // try~catch end
		
	}
	
	// �븞�떖�떇�떦 �젙蹂� 遺덈윭�삤湲�
	@Override
	public List<String> getRestInfo() throws Exception{
			
		return restMapper.getRestInfo();
	}
	
	// �옄媛��젏寃��몴 �떊洹� �벑濡�
	@Override
	public int selfCheck(SelfCheckDTO pDTO) throws Exception{
		
		int res = 0;
		
		if(pDTO == null) {
			pDTO = new SelfCheckDTO();
		}
		
		int success = selfCheckMapper.insertSCInfo(pDTO);
		
		if(success > 0) {
			res = 1;
		}
		
		return res;
	}
	
	// �옄媛��젏寃��몴 �닔�젙
	@Override
	public int updSCInfo(SelfCheckDTO pDTO) throws Exception{
		
		int res = 0;
		
		int success = 0;
		
		success = selfCheckMapper.updSCInfo(pDTO);
		
		if(success > 0) {
			res = 1;
		}else {
			res = 0;
		}
		
		return res;
	}
	
	// �옄媛��젏寃��몴 珥� 媛��닔
	@Override
	public int countSC() throws Exception{
		return selfCheckMapper.countSC();
	}
	
	// �븞�떖�떇�떦 珥� 媛��닔
	@Override
	public int countBoard() throws Exception{
		
		return restMapper.countBoard();
	}

	// �븞�떖�떇�떦 由ъ뒪�듃 �럹�씠吏� 泥섎━
	@Override
	public List<RestDTO> selectBoard(PagingDTO pDTO) throws Exception{
		
		return restMapper.selectBoard(pDTO);
	}

	// �븞�떖�떇�떦 由ъ뒪�듃 寃��깋
	@Override
	public List<RestDTO> getRestSearchList(RestDTO eDTO) throws Exception{
		
		return restMapper.getRestSearchList(eDTO);
	}

	// �븞�떖�떇�떦 �긽�꽭�럹�씠吏�
	@Override
	public RestDTO getRestInfoDetail(RestDTO pDTO) throws Exception{
			
		return restMapper.getRestInfoDetail(pDTO);
	}
	
	// �옄媛��젏寃��몴 �긽�꽭�럹�씠吏� 濡쒕뵫�쓣 �쐞�빐�꽌 �뵒鍮꾩뿉�꽌 媛믪쓣 媛��졇�삤湲�
	public SelfCheckDTO getSCInfo(SelfCheckDTO pDTO) throws Exception{
		
		return selfCheckMapper.getSCInfo(pDTO);
	}
}
