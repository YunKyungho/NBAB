package com.haheeho.adblock.board;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.haheeho.adblock.BoardPerPage;
import com.haheeho.adblock.member.Member;

@Service
public class BoardDAO {
	
	private int boardCount;
	
	@Autowired
	private BoardPerPage bp;
	
	@Autowired
	private SqlSession ss;
	
	
	public void setBoardCount(BoardSearchOption bso) {
		boardCount = ss.getMapper(BoardMapper.class).setBoardCount(bso);
		System.out.println(boardCount);
	}
	
	public BoardSequence boardWrite(Board b) {
		
		BoardSequence bs = null;
		try {
			
			if (ss.getMapper(BoardMapper.class).boardWrite(b) == 1) {
				boardCount ++;
				bs = ss.getMapper(BoardMapper.class).getSequence(b);
			}
			
			return bs;
			
		} catch (Exception e) {
			e.printStackTrace();
			return bs;
		}
	}
	
	public void getBoards(HttpServletRequest req) {
		try {
			int page = Integer.parseInt(req.getParameter("page"));
			int boardPerPage = bp.getBoardPerPage();
			
			int start = (page - 1) * boardPerPage + 1;
			int end = page * boardPerPage;
			int bc = boardCount;
			BoardSearchOption bso = new BoardSearchOption(start, end, "");
			
			String search = req.getParameter("search");
			
			if(search != null) {
				bso.setSearch(search);
				bc = ss.getMapper(BoardMapper.class).setBoardCount(bso);
			}
			
			int pageCount = (int) Math.ceil(bc / (double) boardPerPage);
			req.setAttribute("pageCount", pageCount);
			req.setAttribute("pageHref", "board.go");
			
			List<Board> b = ss.getMapper(BoardMapper.class).getBoards(bso);
			req.setAttribute("board", b);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getBoardsByUsername(HttpServletRequest req) {
		try {
			int page = Integer.parseInt(req.getParameter("page"));
			int boardPerPage = bp.getBoardPerPage();
			
			int start = (page - 1) * boardPerPage + 1;
			int end = page * boardPerPage;
			String search = req.getParameter("search");
			BoardSearchOption bso = new BoardSearchOption(start, end, search);
			
			int bc = ss.getMapper(BoardMapper.class).setBoardCountByUsername(bso);
			
			
			int pageCount = (int) Math.ceil(bc / (double) boardPerPage);
			req.setAttribute("pageCount", pageCount);
			req.setAttribute("pageHref", "board.search.username");
			
			List<Board> b = ss.getMapper(BoardMapper.class).getBoardsByUsername(bso);
			req.setAttribute("board", b);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getContent(HttpServletRequest req, BoardSequence bs) {
		try {
			Board b = ss.getMapper(BoardMapper.class).getContent(bs);
			req.setAttribute("board", b);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public BoardResponseResult modifyContent(Board b) {
		try {
			
			if (ss.getMapper(BoardMapper.class).modifyContent(b) == 1) {
				
				return new BoardResponseResult("수정 성공");
			} else {
				
				return new BoardResponseResult("수정 실패");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return new BoardResponseResult("수정 실패");
		}
	}
	
	public BoardResponseResult deleteContent(BoardSequence bs) {
		
		try {
			
			if(ss.getMapper(BoardMapper.class).deleteContent(bs) == 1) {
				boardCount --;
				return new BoardResponseResult("삭제 성공");
			} else {
				
				return new BoardResponseResult("삭제 실패");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return new BoardResponseResult("삭제 실패");
		}
		
	}
	
	public Boards getBoardsById(Member m) {
		List<Board> b = ss.getMapper(BoardMapper.class).getBoardsByID(m);
		Boards boards = new Boards(b);
		
		return boards;
	}
	
}
