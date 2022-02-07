import React, { useState, useEffect } from 'react';
import axios from 'axios';

export default function FriendsList() {
  const [friendsList, setFriendsList] = useState([]);

  const token = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyIiwicm9sZXMiOlsiUk9MRV9VU0VSIl0sInVzZXJQayI6IjIiLCJpYXQiOjE2NDM4NTQ1MDIsImV4cCI6MTY0NjQ0NjUwMn0.s4B6viyO_tR8lZMUdxW62u82uT08ZltwgEBpuvTBZOQ';
  const getFriendsList = () => {
    axios
      .get('http://i6d104.p.ssafy.io:9999/api/friend/isaccept',
      { 
        headers: {
          "Access-Control-Allow-origin": "*",
          "Authorization": `Bearer ${token}`
        },
        params: {
          applicant: 'user'
        }
      })
      .then((response) => {
        setFriendsList(response.data);
        // console.log(response.data)
      });
  };

  // useEffect(() => {
  //   getFriendsList();
  // }, []);

  return (
    <>
      <h1>Body</h1>
    </>
  );
}