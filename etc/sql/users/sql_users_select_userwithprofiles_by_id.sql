SELECT * FROM feedreader.users as t0
	LEFT JOIN feedreader.userprofiles as t1
		ON t0.l_user_id = t1.l_user_id WHERE t0.l_user_id = 20
		