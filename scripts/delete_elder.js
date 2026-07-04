const fs = require('fs/promises');
const base = 'http://localhost:8080/api';
(async ()=>{
  try{
    const admin = JSON.parse(await fs.readFile('./admin_login.json','utf8'));
    const token = admin.token;
    const elderId = 5;
    const res = await fetch(`${base}/elders/${elderId}`, { method: 'DELETE', headers: { Authorization: `Bearer ${token}` } });
    const text = await res.text();
    console.log('status', res.status, 'body', text);
  }catch(e){
    console.error(e);
    process.exit(1);
  }
})();
