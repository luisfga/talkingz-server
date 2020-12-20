SELECT msg.id, msg.content, msg.dest_id, msg.sender_id, au.user_name, msg.sent_time, msg.msg_status
	FROM public.message as msg inner join public.app_user as au ON msg.sender_id = au.id;