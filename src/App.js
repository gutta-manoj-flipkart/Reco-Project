
import Navbar from './Navbar';
import Home from './Home';
import BlogDetails from './BlogDetails';
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Create from './Create';
import NotFound from './NotFound';
import Backend from './Backend';

function App() {
  return (
    <Router>
    <div className="App">
      <Navbar />
     <div className="content">
      <Routes>
      <Route path="/" element={<Home />}></Route>
      <Route path="/create" element={<Create/>}></Route>
      <Route path="/backend" element={<Backend/>}></Route>
      <Route path="/blogs/:id" element={<BlogDetails />} />
      <Route path="*" element={<NotFound/>} />
      </Routes>
      </div> 
    </div>
    </Router>
  );
}

export default App;
