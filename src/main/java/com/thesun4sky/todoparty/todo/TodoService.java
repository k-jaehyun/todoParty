package com.thesun4sky.todoparty.todo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.thesun4sky.todoparty.user.User;
import com.thesun4sky.todoparty.user.UserDTO;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TodoService {
	private final TodoRepository todoRepository;

	public TodoResponseDTO createPost(TodoRequestDTO dto, User user) {
		Todo todo = new Todo(dto);
		todo.setUser(user);

		todoRepository.save(todo);

		return new TodoResponseDTO(todo);
	}

	public TodoResponseDTO getTodo(Long todoId) {
		Todo todo = todoRepository.findById(todoId)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 할일 ID 입니다."));
		return new TodoResponseDTO(todo);
	}

	public Map<UserDTO, List<TodoResponseDTO>> getUserTodoMap() {
		Map<UserDTO, List<TodoResponseDTO>> userTodoMap = new HashMap<>();

		List<Todo> todoList = todoRepository.findAll(Sort.by(Sort.Direction.DESC, "createDate")); // 작성일 기준 내림차순

		todoList.forEach(todo -> {
			var userDto = new UserDTO(todo.getUser());
			var todoDto = new TodoResponseDTO(todo);
			if (userTodoMap.containsKey(userDto)) {
				// 유저 할일목록에 항목을 추가
				userTodoMap.get(userDto).add(todoDto);
			} else {
				// 유저 할일목록을 새로 추가
				userTodoMap.put(userDto, new ArrayList<>(List.of(todoDto)));
			}
		});

		return userTodoMap;
	}
}
