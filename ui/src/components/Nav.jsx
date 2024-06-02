import { Breadcrumbs } from '@mui/material';
import { Link, useLocation } from 'react-router-dom';
import { colorMap } from "./Colors";
import { BorderBottom } from '@mui/icons-material';

function handleClick(event) {
    event.preventDefault();  
}

const linkStyle = {
    cursor: 'pointer',
    textDecoration: 'none',
};

const getActiveLinkStyle = (item) => ({
    ...linkStyle,
    color: (colorMap[item] || colorMap.defaultColor),  
    fontWeight: 500
});

const data = ['OVERVIEW', 'OTS', 'ROR', 'DOR', 'TOS', 'COI', 'EDO', 'ROS', 'ROD'];

const breadcrumbData = data.map(item => ({
    label: item,
    href: `/${item.toLowerCase()}`,
}));

export default function Nav() {
    const location = useLocation(); 
    return (
        <div style={{ display: 'flex', justifyContent: 'center', marginTop: 40 }}>
            <div role="presentation" onClick={handleClick}>
                <Breadcrumbs maxItems={15} sx={{ fontSize: 24 }}>
                    <Link
                        underline="hover"
                        to="/info"
                        style={location.pathname === '/info' ? getActiveLinkStyle('INFO') : linkStyle}
                    >
                        INFO
                    </Link>
                    {breadcrumbData.map((breadcrumb, index) => (
                        <Link
                            key={index}
                            underline="hover"
                            to={breadcrumb.href}
                            aria-current={location.pathname === breadcrumb.href ? 'page' : undefined}
                            style={location.pathname === breadcrumb.href ? getActiveLinkStyle(breadcrumb.label) : linkStyle}
                        >
                            {breadcrumb.label}
                        </Link>
                    ))}
                </Breadcrumbs>
            </div>
        </div>
    );
}
