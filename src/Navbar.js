import { Link } from "react-router-dom";
const Navbar = () => {
    return ( 
        <nav className="navbar">
            <h1>Albatross</h1>
            <div className="links">
                <Link to="/">Home</Link>
                <Link to="/deployments">Deployments</Link>
                <Link to="/configs">Config buckets</Link>
            </div>
        </nav>
     );
}
 
export default Navbar;