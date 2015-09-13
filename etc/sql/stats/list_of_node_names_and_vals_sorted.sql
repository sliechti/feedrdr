SELECT node_name
    FROM feedreader.feedsourcenodes 
        GROUP BY node_name
        ORDER BY node_name
